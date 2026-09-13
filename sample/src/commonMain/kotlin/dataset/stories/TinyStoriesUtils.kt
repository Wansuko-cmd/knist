package dataset.stories

import com.wsr.knist.core.unwrap
import com.wsr.knist.network.Network
import com.wsr.knist.network.NetworkSerializer
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.Xavier
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.adam.AdamW
import com.wsr.knist.network.output.softmax.softmaxWithLoss
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.attention.attention
import com.wsr.knist.network.process.compute.bias.d2.bias
import com.wsr.knist.network.process.compute.dropout.dropout
import com.wsr.knist.network.process.compute.function.relu.swish
import com.wsr.knist.network.process.compute.norm.layer.d2.layerNorm
import com.wsr.knist.network.process.compute.position.positionEmbedding
import com.wsr.knist.network.process.compute.scale.d2.scale
import com.wsr.knist.network.process.compute.skip.skip
import com.wsr.knist.network.process.reshape.token.tokenEmbedding
import dataset.resource
import kotlin.random.Random
import kotlin.random.nextInt
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.SYSTEM
import okio.buffer
import okio.use

private const val TRAIN_PATH = "stories/TinyStories-train.txt"

private const val VOCAB_SIZE = 3000
private const val EMBEDDING_DIM = 256
const val MAX_LENGTH = 128
private const val NUM_LAYERS = 4
private const val NUM_HEADS = 4
private const val FFN_DIM = EMBEDDING_DIM * 4

private const val BATCH_SIZE = 64
private const val NUM_OF_STORIES = 1000

private const val PAD_INDEX = 0
private const val UNK_INDEX = 1

fun createTinyStoriesModel(seed: Int? = null): Network.Src1.Sink1<List<List<String>>, List<List<String>>> = runBlocking {
    NetworkSerializer.apply {
        register(WordsD1::class)
        register(WordD2::class)
    }

    println("単語リスト生成開始")
    val words: List<String> = createWordList(TRAIN_PATH, VOCAB_SIZE)

    // ニューラルネットワークを構築
    val network = Network.create(
        port = port(
            wordsD1(
                maxLength = MAX_LENGTH,
                words = words,
                unknownIndex = UNK_INDEX,
                paddingIndex = PAD_INDEX,
            ),
        ),
        optimizer = AdamW(
            scheduler = Scheduler.CosineAnnealing(
                minRate = 0.0005f,
                maxRate = 0.001f,
                stepSize = NUM_OF_STORIES,
                warmUp = 200,
                initialRate = 0f,
            ),
        ),
        initializer = Xavier(seed = seed),
    ) { input ->
        input
            .tokenEmbedding(
                vocabSize = words.size,
                tokenSize = EMBEDDING_DIM,
            )
            .positionEmbedding()
            .repeat(NUM_LAYERS) {
                this
                    .skip {
                        this
                            .layerNorm(axis = 1).scale(axis = 1).bias(axis = 1)
                            .attention(numOfHeads = NUM_HEADS, biases = {
                                causal().mask(input, PAD_INDEX.toFloat())
                            })
                            .dropout(ratio = 0.9f)
                    }
                    .skip {
                        this
                            .layerNorm(axis = 1).scale(axis = 1).bias(axis = 1)
                            .affine(neuron = FFN_DIM).bias(axis = 1).swish()
                            .affine(neuron = EMBEDDING_DIM).bias(axis = 1)
                            .dropout(ratio = 0.9f)
                    }
            }
            .layerNorm(axis = 1).scale(axis = 1).bias(axis = 1)
            .affine(neuron = words.size)
            .softmaxWithLoss(
                axis = 1,
                converter = {
                    WordD2(
                        words = words,
                        length = MAX_LENGTH,
                        unknownIndex = UNK_INDEX,
                    )
                },
            )
    }

    println("学習開始")
    FileSystem.SYSTEM.resource(TRAIN_PATH).buffer().use { buffer ->
        generateSequence { buffer.readUtf8Line() }
            .generateStories()
            .flatMap { tokenize(it).toData() }
            // バッチサイズ
            .chunked(BATCH_SIZE)
            // 学習バッチ数
            .take(NUM_OF_STORIES)
            .forEachIndexed { lineIndex, trainData ->
                val inputs = trainData.map { it.first }
                val labels = trainData.map { it.second }
                val random = Random.nextInt(inputs.indices)
                println(
                    """
                            ---------------------------
                            train line: $lineIndex
                            入力例: ${inputs[random]}
                            ラベル: ${labels[random]}
                    """.trimIndent(),
                )

                val loss = network.train(inputs, labels).unwrap()
                println(
                    """
                            loss: $loss
                            ---------------------------
                    """.trimIndent(),
                )
            }
    }

    println("出力確認")
    val story = network.createStories(beginning = "One day, a sheep named Bob was very happy.", maxLength = 300)
    println(story)

    network
}

private suspend fun Network.Src1.Sink1<List<List<String>>, List<List<String>>>.createStories(beginning: String, maxLength: Int): String {
    val text = tokenize(beginning).take(MAX_LENGTH).toMutableList()
    repeat(maxLength) {
        val input = text.takeLast(MAX_LENGTH)
        if (input.last() == "<EOS>") return@repeat
        val expect = this.expect(listOf(input))[0][input.lastIndex]
        text.add(expect)
    }
    return text.joinToString(" ")
        .replace(Regex(" ([!?.]) "), ".\n")
        .replace(Regex(" (,)"), "$1")
}

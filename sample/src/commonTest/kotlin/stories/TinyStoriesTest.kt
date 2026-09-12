@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")

package stories

import com.wsr.knist.core.unwrap
import com.wsr.knist.network.Network
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
import dataset.stories.WordD2
import dataset.stories.createWordList
import dataset.stories.generateStories
import dataset.stories.toData
import dataset.stories.tokenize
import dataset.stories.wordsD1
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.SYSTEM
import okio.buffer
import okio.use

private const val TRAIN_PATH = "stories/TinyStories-train.txt"
private const val VALID_PATH = "stories/TinyStories-valid.txt"

private const val VOCAB_SIZE = 3000
private const val EMBEDDING_DIM = 256
const val MAX_LENGTH = 128
private const val NUM_LAYERS = 2
private const val NUM_HEADS = 8
private const val FFN_DIM = EMBEDDING_DIM * 4

private const val BATCH_SIZE = 64
private const val NUM_OF_STORIES = 1000

private const val PAD_INDEX = 0
private const val UNK_INDEX = 1

class TinyStoriesTest {
    @Test
    fun `TinyStoriesモデルの出力を確認`() = runBlocking {
        println("単語リスト生成開始")
        val words: List<String> = createWordList(TRAIN_PATH, VOCAB_SIZE)
        val network = createModel(words)

        println("学習開始")
        FileSystem.SYSTEM.resource(TRAIN_PATH).buffer().use {
            generateSequence { it.readUtf8Line() }
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
                        "train line: $lineIndex, batch size: ${inputs.size}, input: ${inputs[random]}, label: ${labels[random]}",
                    )
                    network.train(inputs, labels).also { println("loss: ${it.unwrap()}") }
                }
        }

        // テスト
        println("テスト開始")
        var all = 0
        var correct = 0
        FileSystem.SYSTEM.resource(VALID_PATH).buffer().use { buffer ->
            generateSequence { buffer.readUtf8Line() }
                .generateStories()
                .flatMap { tokenize(it).toData() }
                .chunked(100)
                .take(100)
                .forEach { testData ->
                    val inputs = testData.map { it.first }
                    val labels = testData.map { it.second }
                    val expected = network.expect(inputs)

                    val sampleIndex = inputs.size / 2
                    println(
                        """
                            ---------------------------
                            入力: ${inputs[sampleIndex]}
                            予測: ${expected[sampleIndex]}
                            正解ラベル: ${labels[sampleIndex]}
                            ---------------------------
                        """.trimIndent(),
                    )

                    all += expected.size
                    correct += expected.zip(labels).count { (expected, actual) -> expected == actual }
                }
        }

        println("テストケース数: $all, 正解数: $correct")

        val story = network.createStories(beginning = "One day, a sheep named Bob was very happy.", maxLength = 300)
        println(story)
    }

    private fun createModel(words: List<String>) = Network.create(
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
        initializer = Xavier(seed = 0),
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
                            .attention(numOfHeads = NUM_HEADS, biases = { causal().mask(input, PAD_INDEX.toFloat()) })
                            .dropout(0.9f)
                    }
                    .skip {
                        this
                            .layerNorm(axis = 1).scale(axis = 1).bias(axis = 1)
                            .affine(FFN_DIM).bias(axis = 1).swish()
                            .affine(EMBEDDING_DIM).bias(axis = 1)
                            .dropout(0.9f)
                    }
            }
            .layerNorm(axis = 1).scale(axis = 1).bias(axis = 1)
            .affine(words.size)
            .softmaxWithLoss(
                converter = {
                    WordD2(
                        words = words,
                        length = MAX_LENGTH,
                        unknownIndex = UNK_INDEX,
                    )
                },
            )
    }

    private suspend fun Network.Src1.Sink1<List<List<String>>, List<List<String>>>.createStories(beginning: String, maxLength: Int): String {
        val text = tokenize(beginning).take(MAX_LENGTH).toMutableList()
        repeat(maxLength) {
            val input = text.takeLast(MAX_LENGTH)
            if (input.last() == "<EOS>") return@repeat
            val expect = expect(listOf(input))[0][input.lastIndex]
            text.add(expect)
        }
        return text.joinToString(" ")
            .replace(Regex(" ([!?.]) "), ".\n")
            .replace(Regex(" (,)"), "$1")
    }
}

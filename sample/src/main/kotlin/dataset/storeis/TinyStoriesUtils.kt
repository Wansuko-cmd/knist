package dataset.storeis

import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.converter.word.WordD2
import com.wsr.converter.word.wordsD1
import com.wsr.initializer.Xavier
import com.wsr.layer.process.affine.affine
import com.wsr.layer.process.attention.attention
import com.wsr.layer.process.bias.bias
import com.wsr.layer.process.dropout.dropout
import com.wsr.layer.process.function.relu.swish
import com.wsr.layer.process.norm.layer.d2.layerNorm
import com.wsr.layer.process.position.positionEmbedding
import com.wsr.layer.process.skip.skip
import com.wsr.layer.reshape.token.tokenEmbedding
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.adam.AdamW
import com.wsr.output.softmax.softmaxWithLoss
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

private const val TRAIN_PATH = "src/main/resources/stories/TinyStories-train.txt"
private const val VALID_PATH = "src/main/resources/stories/TinyStories-valid.txt"

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
private const val EOS_INDEX = 2

fun createTinyStoriesModel(seed: Int? = null): Network<List<String>, List<String>> {
    println("単語リスト生成開始")
    val words: List<String> = createWordList(TRAIN_PATH, VOCAB_SIZE)

    val network = NetworkBuilder.wordsD1(
        maxLength = MAX_LENGTH,
        words = words,
        unknownIndex = UNK_INDEX,
        paddingIndex = PAD_INDEX,
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
    )
        .tokenEmbedding(
            vocabSize = words.size,
            tokenSize = EMBEDDING_DIM,
        )
        .positionEmbedding()
        .repeat(NUM_LAYERS) {
            this
                .skip {
                    this
                        .layerNorm(axis = 1).bias()
                        .attention(numOfHeads = NUM_HEADS, maskValue = PAD_INDEX)
                        .dropout(0.9f)
                }
                .skip {
                    this
                        .layerNorm(axis = 1).bias()
                        .affine(FFN_DIM).bias().swish()
                        .affine(EMBEDDING_DIM).bias()
                        .dropout(0.9f)
                }
        }
        .layerNorm(axis = 1).bias()
        .affine(words.size)
        .softmaxWithLoss(
            converter = {
                WordD2(
                    words = words,
                    length = MAX_LENGTH,
                    unknownIndex = UNK_INDEX,
                )
            },
            maskValue = PAD_INDEX,
        )

    println("学習開始")
    File(TRAIN_PATH)
        .useLines { lines ->
            lines
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
                    network.train(inputs, labels).also { println("loss: $it") }
                }
        }

    // テスト
    println("テスト開始")
    var all = 0
    var correct = 0
    File(VALID_PATH)
        .useLines { lines ->
            lines
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

    return network
}

private fun Sequence<String>.generateStories(): Sequence<String> = sequence {
    var text = ""
    this@generateStories.forEach {
        text += it
        if (it.contains("<|endoftext|>")) {
            yield(text)
            text = ""
        }
    }
}

private fun List<String>.toData(): List<Pair<List<String>, List<String>>> {
    val input = this.take(MAX_LENGTH)
    val label = this.take(MAX_LENGTH + 1).drop(1)
    return listOf(input to label)
}

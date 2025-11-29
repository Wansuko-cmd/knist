package mnist

import com.wsr.BLAS
import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.NetworkSerializer
import com.wsr.initializer.He
import com.wsr.layer.process.affine.affine
import com.wsr.layer.process.bias.bias
import com.wsr.layer.process.function.relu.reLU
import com.wsr.layer.process.function.relu.swish
import com.wsr.layer.process.norm.layer.d1.layerNorm
import com.wsr.layer.process.skip.skip
import com.wsr.layer.reshape.reshape.reshapeToD1
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.adam.AdamW
import com.wsr.output.softmax.softmaxWithLoss
import dataset.mnist.LabelConverter
import dataset.mnist.PixelConverter
import dataset.mnist.inputPx
import kotlin.random.Random
import kotlin.test.Test
import org.junit.Before

private const val TRAIN_IMAGE_PATH = "train-images-idx3-ubyte.gz"
private const val TRAIN_LABEL_PATH = "train-labels-idx1-ubyte.gz"

private const val TEST_IMAGE_PATH = "train-images-idx3-ubyte.gz"
private const val TEST_LABEL_PATH = "train-labels-idx1-ubyte.gz"

private const val EPOC = 1

private const val SEED = 0
private val RANDOM = Random(SEED)

class MnistTest {
    @Before
    fun setup() {
        // カスタムした層をSerializerに登録
        NetworkSerializer.apply {
            register(PixelConverter::class)
            register(LabelConverter::class)
        }

        println(
            """
                設定
                BLAS is Native: ${BLAS.isNative}
            """.trimIndent(),
        )
    }

    @Test
    fun test() {
        // ニューラルネットワークを構築
        val network = createNetwork()

        println("訓練開始")
        val train = MnistDataset.read(imagePath = TRAIN_IMAGE_PATH, labelPath = TRAIN_LABEL_PATH)
        repeat(EPOC) { epoc ->
            println("epoc: $epoc")
            train.shuffled(RANDOM).chunked(240).mapIndexed { i, data ->
                if (i % 10 == 0) println("train: $i")
                network.train(
                    input = data.map { it.pixels },
                    label = data.map { it.label },
                )
            }
        }

        println("評価開始")
        val test = MnistDataset
            .read(imagePath = TEST_IMAGE_PATH, labelPath = TEST_LABEL_PATH)
            .take(100)

        test
            .count { data -> network.expect(input = data.pixels) == data.label }
            .let { println(it.toFloat() / test.size.toFloat()) }
    }

    private fun createNetwork(): Network<List<Float>, Int> = NetworkBuilder
        .inputPx(
            x = 28,
            y = 28,
            optimizer = AdamW(scheduler = Scheduler.Fix(0.001f)),
            initializer = He(seed = SEED),
        )
        .reshapeToD1()
        .affine(neuron = 512).bias().reLU()
        .repeat(5) {
            skip {
                this
                    .layerNorm().affine(neuron = 512).bias().swish()
                    .layerNorm().affine(neuron = 512).bias().swish()
            }
        }
        .skip {
            this
                .layerNorm().affine(neuron = 512).bias().swish()
                .layerNorm().affine(neuron = 128).bias().swish()
        }
        .affine(neuron = 10)
        .softmaxWithLoss(converter = { LabelConverter(inputSize) })
}

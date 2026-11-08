@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")

package mnist

import com.wsr.knist.network.Network
import com.wsr.knist.network.NetworkSerializer
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.He
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.adam.AdamW
import com.wsr.knist.network.output.softmax.softmaxWithLoss
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.attention.attention
import com.wsr.knist.network.process.compute.bias.d1.bias
import com.wsr.knist.network.process.compute.bias.d2.bias as biasD2
import com.wsr.knist.network.process.compute.bias.d3.bias as biasD3
import com.wsr.knist.network.process.compute.conv.convD2
import com.wsr.knist.network.process.compute.dropout.dropout
import com.wsr.knist.network.process.compute.function.relu.reLU
import com.wsr.knist.network.process.compute.function.relu.swish
import com.wsr.knist.network.process.compute.norm.layer.d1.layerNorm
import com.wsr.knist.network.process.compute.norm.rms.d2.rmsNorm
import com.wsr.knist.network.process.compute.pool.maxPool
import com.wsr.knist.network.process.compute.skip.skip
import com.wsr.knist.network.process.reshape.reshape.reshapeToD1
import com.wsr.knist.network.process.reshape.reshape.reshapeToD2
import com.wsr.knist.network.process.reshape.reshape.reshapeToD3
import dataset.mnist.LabelConverter
import dataset.mnist.MnistData
import dataset.mnist.PixelConverter
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.runBlocking

private const val TRAIN_IMAGE_PATH = "mnist/train-images-idx3-ubyte.gz"
private const val TRAIN_LABEL_PATH = "mnist/train-labels-idx1-ubyte.gz"

private const val TEST_IMAGE_PATH = "mnist/t10k-images-idx3-ubyte.gz"
private const val TEST_LABEL_PATH = "mnist/t10k-labels-idx1-ubyte.gz"

class MnistTest {
    @Test
    fun `Mnistモデルの精度が落ちていないか確認`() = runBlocking {
        NetworkSerializer.apply {
            register(PixelConverter::class)
            register(LabelConverter::class)
        }

        println("ネットワーク構築")
        val network = createNetwork()

        println("Json変換")
        network.toJson()
            .also { println(it.take(100) + "...") }
            .also { println(Network.Src1.Sink1.fromJson<List<List<Float>>, List<Int>>(it)) }

        println("訓練開始")
        val train = MnistData.read(imagePath = TRAIN_IMAGE_PATH, labelPath = TRAIN_LABEL_PATH)
        val trainTime = measureTime {
            repeat(2) { epoch ->
                println("epoch: $epoch")
                train.chunked(240).forEachIndexed { i, data ->
                    if (i % 10 == 0) println("train: $i")
                    network.train(
                        input = data.map { it.pixels },
                        label = data.map { it.label },
                    )
                }
            }
        }

        println("評価開始")
        val test = MnistData
            .read(imagePath = TEST_IMAGE_PATH, labelPath = TEST_LABEL_PATH)
            .take(1000)

        val (accuracy, evalTime) = measureTimedValue {
            test.count { data -> network.expect(input = listOf(data.pixels))[0] == data.label }
                .let { it.toFloat() / test.size.toFloat() }
        }

        println("${accuracy * 100}%")

        println("KNIST_METRIC time_ms=${(trainTime + evalTime).inWholeMilliseconds}")

        assertTrue(actual = accuracy > 0.95f, message = "精度が95%を割っています")
    }

    private fun createNetwork(): Network.Src1.Sink1<List<List<Float>>, List<Int>> = Network.create(
        port = port(PixelConverter(outputI = 28, outputJ = 28)),
        optimizer = AdamW(scheduler = Scheduler.Fix(0.001f)),
        initializer = He(seed = 0),
    ) { input ->
        input
            .reshapeToD3(i = 1)
            .convD2(filter = 16, kernel = 5, padding = 2).biasD3().reLU()
            .maxPool(size = 2)
            .convD2(filter = 32, kernel = 3, padding = 1).biasD3().reLU()
            .maxPool(size = 2)
            .reshapeToD2(i = 49, j = 32)
            .repeat(2) {
                skip {
                    this.rmsNorm().attention(numOfHeads = 4).dropout(ratio = 0.9f, seed = 0)
                }
                    .skip {
                        this.rmsNorm().affine(neuron = 64).biasD2().swish().affine(neuron = 32).biasD2()
                    }
            }
            .reshapeToD1()
            .layerNorm().affine(neuron = 128).bias().swish()
            .affine(neuron = 10)
            .softmaxWithLoss(converter = { LabelConverter(inputI) })
    }
}

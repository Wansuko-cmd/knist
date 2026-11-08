package dataset.mnist

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.elementwise.compare.eq
import com.wsr.knist.batch.reduction.maxIndex
import com.wsr.knist.batch.shape.toD1
import com.wsr.knist.core.IOType
import com.wsr.knist.core.reduction.sum
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.Network
import com.wsr.knist.network.converter.raw.RawD3
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.He
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.adam.AdamW
import com.wsr.knist.network.output.softmax.softmaxWithLoss
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.bias.d1.bias
import com.wsr.knist.network.process.compute.bias.d3.bias
import com.wsr.knist.network.process.compute.conv.convD2
import com.wsr.knist.network.process.compute.function.relu.reLU
import com.wsr.knist.network.process.compute.norm.layer.d1.layerNorm
import com.wsr.knist.network.process.reshape.reshape.reshapeToD1
import kotlinx.coroutines.runBlocking

private const val TRAIN_IMAGE_PATH = "mnist/train-images-idx3-ubyte.gz"
private const val TRAIN_LABEL_PATH = "mnist/train-labels-idx1-ubyte.gz"

private const val TEST_IMAGE_PATH = "mnist/t10k-images-idx3-ubyte.gz"
private const val TEST_LABEL_PATH = "mnist/t10k-labels-idx1-ubyte.gz"

fun createMnistModel(epoch: Int, seed: Int? = null): Network.Src1.Sink1<Batch<IOType.D3>, Batch<IOType.D1>> = runBlocking {
    println("データ読み込み")
    val train = MnistDatasets.read(imagePath = TRAIN_IMAGE_PATH, labelPath = TRAIN_LABEL_PATH)
    val test = MnistDatasets.read(imagePath = TEST_IMAGE_PATH, labelPath = TEST_LABEL_PATH)

    // ニューラルネットワークを構築
    val network = Network.create(
        port = port(RawD3(outputI = 1, outputJ = 28, outputK = 28)),
        optimizer = AdamW(scheduler = Scheduler.Fix(0.001f)),
        initializer = He(seed = seed),
    ) { input ->
        input
            .convD2(filter = 16, kernel = 5, padding = 2).bias().reLU()
            .convD2(filter = 32, kernel = 3, padding = 1).bias().reLU()
            .reshapeToD1()
            .layerNorm()
            .affine(neuron = 128).bias().reLU()
            .affine(neuron = 10)
            .softmaxWithLoss()
    }

    println("訓練開始")
    repeat(epoch) { epoch ->
        println("epoch: $epoch")
        train.chunked(256).forEach { data ->
            network.train(input = data.toImages(), label = data.toLabels())
        }
    }

    println("評価開始")
    var count = 0
    test.chunked(264).forEach { data ->
        val expect = network.expect(data.toImages())
        val actual = data.toLabels()
        val correct = (actual.maxIndex() eq expect.maxIndex())
        count += correct.toD1().sum().unwrap().toInt()
    }
    println("${count / test.size.toFloat()}")

    network
}

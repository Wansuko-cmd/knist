package dataset.mnist

import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.NetworkSerializer
import com.wsr.initializer.He
import com.wsr.layer.compute.affine.affine
import com.wsr.layer.compute.bias.bias
import com.wsr.layer.compute.function.relu.reLU
import com.wsr.layer.compute.function.relu.swish
import com.wsr.layer.compute.norm.layer.d1.layerNorm
import com.wsr.layer.compute.skip.skip
import com.wsr.layer.reshape.reshape.reshapeToD1
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.adam.AdamW
import com.wsr.output.softmax.softmaxWithLoss

private const val TRAIN_IMAGE_PATH = "mnist/train-images-idx3-ubyte.gz"
private const val TRAIN_LABEL_PATH = "mnist/train-labels-idx1-ubyte.gz"

private const val TEST_IMAGE_PATH = "mnist/t10k-images-idx3-ubyte.gz"
private const val TEST_LABEL_PATH = "mnist/t10k-labels-idx1-ubyte.gz"

fun createMnistModel(epoc: Int, seed: Int? = null): Float {
    // カスタムした層をSerializerに登録
    NetworkSerializer.apply {
        register(PixelConverter::class)
        register(LabelConverter::class)
    }

    // ニューラルネットワークを構築
    val network = createNetwork(seed)

    network.toJson()

    println("訓練開始")
    val train = MnistDataset.read(imagePath = TRAIN_IMAGE_PATH, labelPath = TRAIN_LABEL_PATH)
    repeat(epoc) { epoc ->
        println("epoc: $epoc")
        train.chunked(240).mapIndexed { i, data ->
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

    val accuracy = test
        .count { data -> network.expect(input = data.pixels) == data.label }
        .let { it.toFloat() / test.size.toFloat() }

    println("${accuracy * 100}%")

    return accuracy
}

private fun createNetwork(seed: Int?): Network<List<Float>, Int> = NetworkBuilder
    .inputPx(
        x = 28,
        y = 28,
        optimizer = AdamW(scheduler = Scheduler.Fix(0.001f)),
        initializer = He(seed = seed),
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

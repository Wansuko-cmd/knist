package dataset.mnist

import com.wsr.NetworkBuilder
import com.wsr.NetworkSerializer
import com.wsr.initializer.He
import com.wsr.layer.output.softmax.softmaxWithLoss
import com.wsr.layer.process.affine.affine
import com.wsr.layer.process.bias.bias
import com.wsr.layer.process.function.relu.reLU
import com.wsr.layer.process.norm.layer.layerNorm
import com.wsr.layer.process.skip.skip
import com.wsr.layer.reshape.reshape.reshapeToD1
import com.wsr.optimizer.adam.AdamW
import java.util.Random

fun createMnistModel(epoc: Int, seed: Int? = null) {
    // カスタムした層をSerializerに登録
    NetworkSerializer.apply {
        register(PixelConverter::class)
        register(LabelConverter::class)
    }

    // ニューラルネットワークを構築
    val network = NetworkBuilder
        .inputPx(x = 28, y = 28, optimizer = AdamW(0.001), initializer = He(seed = seed), seed = seed)
        .reshapeToD1()
        .affine(neuron = 512).bias().reLU()
        .repeat(5) {
            skip {
                this
                    .layerNorm().affine(neuron = 512).bias().reLU()
                    .layerNorm().affine(neuron = 512).bias().reLU()
            }
        }
        .skip {
            this
                .layerNorm().affine(neuron = 512).bias().reLU()
                .layerNorm().affine(neuron = 128).bias().reLU()
        }
        .affine(neuron = 10)
        .softmaxWithLoss(converter = { LabelConverter(inputSize) })

    // テストデータを用意
    val random = seed?.let { Random(seed.toLong()) } ?: Random()
    val dataset = MnistDataset.read().shuffled(random)
    val (train, test) = dataset.take(50000) to dataset.takeLast(10000).take(100)

    // 学習
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled(random).take(5000).chunked(240).mapIndexed { i, data ->
            network.train(
                input = data.map { it.pixels },
                label = data.map { it.label },
            )
            println("train: $i")
        }
    }

    // 予測
    test
        .count { data -> network.expect(input = data.pixels) == data.label }
        .let { println(it.toDouble() / test.size.toDouble()) }
}

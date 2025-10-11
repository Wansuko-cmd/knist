package dataset.mnist

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.optimizer.sgd.Sgd
import com.wsr.output.softmax.softmaxWithLoss
import com.wsr.process.affine.affine
import com.wsr.process.bias.bias
import com.wsr.process.conv.convD1
import com.wsr.process.function.relu.reLU
import com.wsr.reshape.reshapeToD1
import java.util.Random
import maxIndex

fun createMnistModel(epoc: Int, seed: Int? = null) {
    val network = NetworkBuilder
        .inputD2(x = 28, y = 28, optimizer = Sgd(0.01), seed = seed)
        .convD1(filter = 16, kernel = 3).bias().reLU()
        .convD1(filter = 32, kernel = 3).bias().reLU()
        .reshapeToD1()
        .affine(neuron = 512).bias().reLU()
        .affine(neuron = 10)
        .softmaxWithLoss()

    val random = seed?.let { Random(seed.toLong()) } ?: Random()

    val dataset = MnistDataset.read().shuffled(random)
    val (train, test) = dataset.take(50000) to dataset.takeLast(10000).take(100)
    println("${dataset.size}")

    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled(random).take(5000).chunked(240).mapIndexed { i, data ->
            network.train(
                input = data.map { IOType.d2(listOf(28, 28), it.pixels) },
                label = data.map { (_, label) -> IOType.d1(10) { if (label == it) 1.0 else 0.0 } },
            )
            println("train: $i")
        }
    }
    test
        .count { data ->
            network
                .expect(
                    input =
                    IOType.d2(
                        listOf(28, 28),
                        data.pixels.toMutableList(),
                    ),
                ).value
                .toTypedArray()
                .also { println(it.toList()) }
                .maxIndex() == data.label
        }.let { println(it.toDouble() / test.size.toDouble()) }
}

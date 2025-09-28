package dataset.mnist

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.affine.affine
import com.wsr.process.bias.bias
import com.wsr.process.conv.convD1
import com.wsr.process.function.relu.reLU
import com.wsr.process.pool.maxPool
import com.wsr.reshape.reshapeToD1
import com.wsr.output.softmax.softmaxWithLoss
import maxIndex
import java.util.Random

fun createMnistModel(epoc: Int, seed: Int? = null) {
    val network = NetworkBuilder.inputD2(x = 28, y = 28, rate = 0.01, seed = seed)
//        .affine(100).bias().swish().maxPool(2)
        .convD1(filter = 30, kernel = 4, stride = 2, padding = 2).bias().reLU().maxPool(3)
        .reshapeToD1()
        .affine(neuron = 512)
        .bias().reLU()
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
                input = data.map { IOType.d2(listOf(28, 28), it.pixels) } ,
                label = data.map { (_, label) -> IOType.d1(10) { if (label == it) 1.0 else 0.0 } },
            )
            println("train: $i")
        }
    }
    test.count { data ->
        network.expect(
            input = IOType.d2(
                listOf(28, 28),
                data.pixels.toMutableList(),
            ),
        ).value.also { println(it.toList()) }.maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}
package dataset.mnist

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layers.affine.affine
import com.wsr.layers.bias.bias
import com.wsr.layers.conv.convD1
import com.wsr.layers.function.relu.relu
import com.wsr.layers.function.softmax.softmax
import com.wsr.layers.pool.maxPool
import maxIndex
import java.util.Random

fun createMnistModel(epoc: Int, seed: Int? = null) {
    val network = NetworkBuilder.inputD2(x = 28, y = 28, rate = 0.01, seed = seed)
//        .convD1(filter = 30, kernel = 5, stride = 1, padding = 0).bias().relu().maxPool(2)
        .affine(50)
        .reshapeD1()
        .affine(neuron = 512).bias().relu()
        .affine(neuron = 10).softmax()
        .build()

    val random = seed?.let { Random(seed.toLong()) } ?: Random()

    val dataset = MnistDataset.read().shuffled(random)
    val (train, test) = dataset.take(50000) to dataset.takeLast(10000).take(100)
    println("${dataset.size}")

    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled(random).chunked(240).mapIndexed { i, data ->
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
        ).value.maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}
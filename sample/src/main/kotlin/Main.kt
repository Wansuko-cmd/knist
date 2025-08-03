import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.affine.affine
import com.wsr.layers.bias.bias
import com.wsr.layers.conv.convD1
import com.wsr.layers.function.relu.relu
import com.wsr.layers.function.softmax.softmax
import com.wsr.layers.pool.maxPool
import dataset.iris.irisDatasets
import dataset.mnist.MnistDataset
import java.util.Random

private const val EPOC = 1
val random = Random(2)

fun main() {
    val network = NetworkBuilder.inputD2(x = 28, y = 28, rate = 0.01, seed = 3)
        .convD1(filter = 30, kernel = 5, stride = 1, padding = 0).bias().relu().maxPool(2)
        .convD1(filter = 30, kernel = 5, stride = 1, padding = 0).bias().relu().maxPool(2)
        .reshapeD1()
        .affine(neuron = 512).bias().relu()
        .affine(neuron = 10).softmax()
        .build()

    val dataset = MnistDataset.read().shuffled(random)
    val (train, test) = dataset.take(50000) to dataset.takeLast(10000).take(100)
    println("${dataset.size}")

    (1..EPOC).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled(random).take(230).forEachIndexed { i, data ->
            network.train(
                input = IOType.D2(
                    data.pixels.toMutableList(),
                    listOf(28, 28),
                ),
                label = IOType.D1(10) { if (data.label == it) 1.0 else 0.0 },
            )
            if (i % 100 == 0) println("trained: $i")
        }
    }
    test.count { data ->
        network.expect(
            input = IOType.D2(
                data.pixels.toMutableList(),
                listOf(28, 28),
            ),
        ).value.maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

private fun iris() {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network = NetworkBuilder.inputD2(x = 1, y = 4, rate = 0.01)
        .convD1(filter = 1, kernel = 1, stride = 2, padding = 2).relu()
        .reshapeD1()
        .affine(neuron = 50).bias().relu()
        .affine(neuron = 3).softmax()
        .build()

    (1..EPOC).forEach { epoc ->
        train.forEach { data ->
            network.train(
                input = IOType.D2(
                    mutableListOf(
                        data.petalLength,
                        data.petalWidth,
                        data.sepalLength,
                        data.sepalWidth,
                    ),
                    listOf(1, 4),
                ),
                label = IOType.D1(3) { if (data.label == it) 1.0 else 0.0 },
            )
        }
    }
    test.count { data ->
        network.expect(
            input = IOType.D2(
                mutableListOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalLength,
                    data.sepalWidth,
                ),
                listOf(4, 1),
            ),
        ).value.maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

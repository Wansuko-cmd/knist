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

private const val EPOC = 3

fun main() {
    val dataset = MnistDataset.read()
    val (train, test) = dataset.shuffled() to dataset.shuffled().take(100)
    val network = NetworkBuilder.inputD2(x = 1, y = 784, rate = 0.01)
        .convD1(filter = 3, kernel = 6, stride = 2).bias().relu().maxPool(2)
        .reshapeD1()
        .affine(neuron = 512).bias().relu()
        .affine(neuron = 10).softmax()
        .build()

    println("${dataset.size}")
    (1..EPOC).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled().take(1000).forEachIndexed { i, data ->
            network.train(
                input = IOType.D2(
                    data.pixels.toMutableList(),
                    listOf(1, 784),
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
                listOf(1, 784),
            ),
        ).value.maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

private fun iris() {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network = NetworkBuilder.inputD2(x = 1, y = 4, rate = 0.01)
        .convD1(filter = 1, kernel = 1, stride = 2).relu()
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

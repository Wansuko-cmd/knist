import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.affine.affine
import com.wsr.layers.bias.bias
import com.wsr.layers.function.relu.relu
import com.wsr.layers.function.softmax.softmax
import dataset.iris.irisDatasets

private const val EPOC = 1000

fun main() {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network = NetworkBuilder.inputD2(x = 4, y = 1, rate = 0.01)
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
                    listOf(4, 1),
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

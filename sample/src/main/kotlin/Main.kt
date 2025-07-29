import com.wsr.Network
import com.wsr.layers.affine.affineD1
import com.wsr.layers.bias.biasD1
import com.wsr.layers.function.reluD1
import com.wsr.layers.function.softmaxD1
import dataset.iris.irisDatasets

private const val EPOC = 1000

fun main() {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network = Network.Builder(numOfInput = 4, rate = 0.01)
        .affineD1(neuron = 50).biasD1().reluD1()
        .affineD1(neuron = 3).softmaxD1()
        .build()

    (1..EPOC).forEach { epoc ->
        train.forEach { data ->
            network.train(
                input = arrayOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalLength,
                    data.sepalWidth,
                ),
                label = Array(3) { if (data.label == it) 1.0 else 0.0 },
            )
        }
    }
    test.count { data ->
        network.expect(
            input = arrayOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ).toList().maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

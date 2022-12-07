import common.relu
import common.sigmoid
import dataset.iris.irisDatasets
import kotlin.random.Random
import network.InputConfig
import network.LayerConfig
import network.LayerType
import network.Network

fun main() {
    val (train, test) = irisDatasets.shuffled().chunked(120)
    val network = Network.create(
        InputConfig(4),
        listOf(
            LayerConfig(50, ::relu, LayerType.Affine),
            LayerConfig(3, ::sigmoid, LayerType.Affine),
        ),
        random = Random(0),
        rate = 0.01,
    )
    (1..1000).forEach { epoc ->
        println("epoc: $epoc")
        train.forEach { data ->
            network.train(
                input = listOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalLength,
                    data.sepalWidth,
                ),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expect(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ) == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}
package dataset.iris

import common.relu
import common.sigmoid
import layers.InputConfig
import layers.LayerConfig
import layers.OutputConfig
import layers.affine.Affine
import network.Network
import kotlin.random.Random

fun createIrisModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = irisDatasets.shuffled().chunked(120)
    val network = Network.create(
        InputConfig(4),
        listOf(
            LayerConfig(50, ::relu, Affine),
        ),
        OutputConfig(3, ::sigmoid),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
//        println("epoc: $epoc")
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

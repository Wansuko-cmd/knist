package dataset.iris

import common.relu
import common.sigmoid
import network.Network
import kotlin.random.Random
import layers.layer0d.Affine
import layers.layer0d.Input0dConfig
import layers.layer0d.Layer0dConfig
import layers.layer0d.Output0dConfig

fun createIrisModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = irisDatasets.shuffled().chunked(120)
    val network = Network.create0d(
        Input0dConfig(4),
        listOf(
            Layer0dConfig(50, ::relu, Affine),
        ),
        Output0dConfig.Sigmoid(3, Affine),
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

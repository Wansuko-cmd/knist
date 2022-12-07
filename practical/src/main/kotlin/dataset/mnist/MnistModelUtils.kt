package dataset.mnist

import common.relu
import kotlin.random.Random
import layers.layer0d.Affine
import layers.layer0d.Layer0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Conv1d
import layers.layer1d.Input1dConfig
import layers.layer1d.Layer1dConfig
import network.Network

fun createMnistModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = MnistDataset.read().shuffled().chunked(2000)
    val network = Network.create1d(
        Input1dConfig(1),
        listOf(
            Layer1dConfig(
                20,
                train.first().imageSize * train.first().imageSize,
                5,
                ::relu,
                Conv1d,
            ),
            Layer0dConfig(50, ::relu, Affine),
        ),
        Output0dConfig.Softmax(3),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 100 == 0) println("i: $index")
            network.train(
                input = listOf(data.pixels),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expect(
            input = listOf(data.pixels),
        ) == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

package dataset.mnist

import common.relu
import layers.layer0d.Affine
import layers.layer0d.Layer0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Conv1d
import layers.layer1d.Input1dConfig
import layers.layer1d.Layer1dConfig
import network.Network
import kotlin.random.Random

fun createMnistModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = MnistDataset.read().shuffled().chunked(3000)
    val network = Network.create1d(
        inputConfig = Input1dConfig(channel = 1, inputSize = train.first().imageSize * train.first().imageSize),
        centerConfig = listOf(
            Layer1dConfig(
                channel = 32,
                kernelSize = 5,
                activationFunction = ::relu,
                type = Conv1d,
            ),
            Layer0dConfig(
                numOfNeuron = 50,
                activationFunction = ::relu,
                type = Affine,
            ),
        ),
        outputConfig = Output0dConfig.Softmax(10),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 100 == 0) println("i: $index")
            network.train(input = listOf(data.pixels), label = data.label)
        }
    }
    test.count { data ->
        network.expect(input = listOf(data.pixels)) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

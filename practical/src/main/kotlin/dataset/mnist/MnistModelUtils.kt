package dataset.mnist

import common.identity
import common.relu
import layers.affine.Affine
import layers.bias.Bias0d
import layers.input.Input0dLayer
import layers.output.layer0d.Softmax0d
import layers.bias.Bias1d
import layers.conv.Conv1d
import layers.input.Input1dLayer
import network.Network
import kotlin.random.Random

fun createMnistModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = MnistDataset.read().shuffled().chunked(40000)
    val network = Network.create1d(
        inputConfig = Input1dLayer(channel = 1, inputSize = train.first().imageSize * train.first().imageSize),
        centerConfig = listOf(
            Conv1d(
                channel = 4,
                kernelSize = 2,
                activationFunction = ::identity,
            ),
            Bias1d(::relu),
            Affine(
                numOfNeuron = 50,
                activationFunction = ::identity,
            ),
            Bias0d(::relu)
        ),
        outputConfig = Softmax0d(10) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 1000 == 0) println("i: $index")
            network.train(input = listOf(data.pixels), label = data.label)
        }
    }
    test.count { data ->
        network.expect(input = listOf(data.pixels)) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

fun createMnistModel0d(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = MnistDataset.read().shuffled().chunked(20000)
    val network = Network.create0d(
        inputConfig = Input0dLayer(train.first().imageSize * train.first().imageSize),
        centerConfig = listOf(
            Affine(numOfNeuron = 50, activationFunction = ::relu),
        ),
        outputConfig = Softmax0d(10) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 10000 == 0) println("i: $index")
            network.train(input = data.pixels, label = data.label)
        }
    }
    test.count { data ->
        network.expect(input = data.pixels) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

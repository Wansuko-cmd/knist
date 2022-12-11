package dataset.signal

import common.relu
import dataset.mnist.MnistDataset
import layers.layer0d.Affine
import layers.layer0d.Input0dConfig
import layers.layer0d.Layer0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Conv1d
import layers.layer1d.Input1dConfig
import layers.layer1d.Layer1dConfig
import network.Network
import kotlin.random.Random

fun createSignalModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = signalDatasets.shuffled().chunked((signalDatasets.size * 0.8).toInt())
    val network = Network.create1d(
        inputConfig = Input1dConfig(channel = 1, inputSize = train.first().signal.size),
        centerConfig = listOf(
            Layer1dConfig(
                channel = 32,
                kernelSize = 5,
                activationFunction = ::relu,
                type = Conv1d,
            ),
            Layer1dConfig(
                channel = 64,
                kernelSize = 5,
                activationFunction = ::relu,
                type = Conv1d,
            ),
            Layer0dConfig(
                numOfNeuron = 50,
                activationFunction = ::relu,
                type = Affine,
            ),
            Layer0dConfig(
                numOfNeuron = 32,
                activationFunction = ::relu,
                type = Affine,
            ),
        ),
        outputConfig = Output0dConfig.Sigmoid(2, Affine),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 10000 == 0) println("i: $index")
            network.train(input = listOf(data.signal), label = data.label)
        }
    }
    test.count { data ->
        network.expect(input = listOf(data.signal)) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

fun createSignalModel0d(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = signalDatasets.shuffled().chunked((signalDatasets.size * 0.8).toInt())
    val network = Network.create0d(
        inputConfig = Input0dConfig(train.first().signal.size),
        centerConfig = listOf(
            Layer0dConfig(numOfNeuron = 50, activationFunction = ::relu, type = Affine),
        ),
        outputConfig = Output0dConfig.Softmax(2, Affine),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEachIndexed { index, data ->
            if (index % 10000 == 0) println("i: $index")
            network.train(input = data.signal, label = data.label)
        }
    }
    test.count { data ->
        network.expect(input = data.signal) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

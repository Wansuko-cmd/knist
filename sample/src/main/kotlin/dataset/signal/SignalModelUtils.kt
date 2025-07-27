package dataset.signal

import com.wsr.common.identity
import com.wsr.common.relu
import com.wsr.layers.affine.Affine
import com.wsr.layers.bias.Bias0d
import com.wsr.layers.bias.Bias1d
import com.wsr.layers.conv.Conv1d
import com.wsr.layers.input.Input0dLayer
import com.wsr.layers.output.layer0d.Sigmoid0d
import com.wsr.layers.input.Input1dLayer
import com.wsr.Network
import kotlin.random.Random

fun createSignalModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = signalDatasets.shuffled().chunked((signalDatasets.size * 0.8).toInt())
    val network = Network.create1d(
        inputConfig = Input1dLayer(channel = 1, inputSize = train.first().signal.size),
        centerConfig = listOf(
            Conv1d(
                channel = 32,
                kernelSize = 5,
                activationFunction = ::identity,
                padding = 4,
                stride = 1,
            ),
            Bias1d(::relu),
            Conv1d(
                channel = 64,
                kernelSize = 5,
                activationFunction = ::identity,
                padding = 4,
                stride = 1,
            ),
            Bias1d(::relu),
            Affine(
                numOfNeuron = 50,
                activationFunction = ::identity,
            ),
            Bias0d(::relu),
            Affine(
                numOfNeuron = 32,
                activationFunction = ::identity,
            ),
            Bias0d(::relu),
        ),
        outputConfig = Sigmoid0d(2) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
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
        inputConfig = Input0dLayer(train.first().signal.size),
        centerConfig = listOf(
            Affine(numOfNeuron = 50, activationFunction = ::relu),
        ),
        outputConfig = Sigmoid0d(2) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
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

package dataset.iris

import common.identity
import common.relu
import layers.affine.Affine
import layers.bias.Bias0d
import layers.input.Input0dLayer
import layers.output.layer0d.Softmax0d
import network.Network
import kotlin.random.Random

fun createIrisModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = irisDatasets.shuffled().chunked(120)
    val network = Network.create0d(
        Input0dLayer(4),
        listOf(
            Affine(50, ::identity),
            Bias0d(::relu)
        ),
        Softmax0d(3) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
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

package dataset.wine

import common.relu
import layers.affine.Affine
import layers.input.Input0dLayer
import layers.output.layer0d.Softmax0d
import network.Network
import kotlin.random.Random

fun createWineModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = wineDatasets.shuffled().map { it.centering() }.chunked(120)
    val network = Network.create0d(
        Input0dLayer(13),
        listOf(
            Affine(50, ::relu),
        ),
        Softmax0d(3) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
    (1..epoc).forEach { epoc ->
        train.forEach { data ->
            network.train(
                input = listOf(
                    data.alcohol,
                    data.malicAcid,
                    data.ash,
                    data.alcalinityOfAsh,
                    data.magnesium,
                    data.totalPhenols,
                    data.flavanoids,
                    data.nonflavAnoidPhenols,
                    data.proanthocyanins,
                    data.colorIntensity,
                    data.hue,
                    data.wines,
                    data.proline,
                ),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expect(
            input = listOf(
                data.alcohol,
                data.malicAcid,
                data.ash,
                data.alcalinityOfAsh,
                data.magnesium,
                data.totalPhenols,
                data.flavanoids,
                data.nonflavAnoidPhenols,
                data.proanthocyanins,
                data.colorIntensity,
                data.hue,
                data.wines,
                data.proline,
            ),
        ) == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

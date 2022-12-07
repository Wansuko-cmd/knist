package dataset.wine

import common.relu
import common.sigmoid
import layers.InputConfig
import layers.LayerConfig
import layers.LayerType
import network.Network
import kotlin.random.Random
import layers.OutputConfig
import layers.affine.Affine

fun createWineModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = wineDatasets.shuffled().map { it.centering() }.chunked(120)
    val network = Network.create(
        InputConfig(13),
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

package dataset.wine

import common.relu
import common.sigmoid
import network.InputConfig
import network.LayerConfig
import network.LayerType
import network.Network
import kotlin.random.Random

fun createWineModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = wineDatasets.shuffled().map { it.centering() }.chunked(120)
    val network = Network.create(
        InputConfig(13),
        listOf(
            LayerConfig(50, ::relu, LayerType.Affine),
            LayerConfig(3, ::sigmoid, LayerType.Affine),
        ),
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

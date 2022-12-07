package dataset.wine

import common.relu
import common.sigmoid
import layers.layer0d.Affine
import layers.layer0d.Layer0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Conv1d
import layers.layer1d.Input1dConfig
import layers.layer1d.Layer1dConfig
import network.Network
import kotlin.random.Random

fun createWineModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = wineDatasets.shuffled().map { it.centering() }.chunked(120)
    val network = Network.create1d(
        Input1dConfig(1),
        listOf(
            Layer1dConfig(10, 13, 5, ::relu, Conv1d),
            Layer0dConfig(50, ::relu, Affine),
        ),
        Output0dConfig(3, ::sigmoid),
        random = seed?.let { Random(it) } ?: Random,
        rate = 0.01,
    )
//    val network = Network.create0d(
//        Input0dConfig(13),
//        listOf(
//            Layer0dConfig(50, ::relu, Affine),
//        ),
//        Output0dConfig(3, ::sigmoid),
//        random = seed?.let { Random(it) } ?: Random,
//        rate = 0.01,
//    )
    (1..epoc).forEach { epoc ->
        train.forEach { data ->
            network.trains(
                input = listOf(
                    listOf(
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
                ),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expects(
            input = listOf(
                listOf(
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
            ),
        ) == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

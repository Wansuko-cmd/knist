@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.mnist.MnistDataset
import dataset.wine.WineDataset
import dataset.wine.wineDatasets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import network.DevNetwork
import network.InputConfig
import network.LayerConfig
import network.LayerType
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val (train, test) = wineDatasets.map { it.centering() }.shuffled().chunked(120)
    createWineModel(train, test, 10000, 19)
//    val (train, test) = MnistDataset.read().chunked(20000)
//    val network = DevNetwork.create(
//        InputConfig(1),
//        listOf(
//            LayerConfig(32, ::relu, LayerType.Conv),
//            LayerConfig(64, ::relu, LayerType.Conv),
//            LayerConfig(30, ::relu, LayerType.MatMul),
//            LayerConfig(10, ::sigmoid, LayerType.MatMul),
//        ),
//        Random(1652),
//        0.01,
//    )
//    (1..3).forEach { epoc ->
//        println("epoc: $epoc")
//        train.forEach { data ->
//            network.trains(
//                input = listOf(data.pixels.chunked(train.first().imageSize)),
//                label = data.label,
//            )
//        }
//    }
//    test.count { data ->
//        network.expects(
//            input = listOf(data.pixels.chunked(train.first().imageSize)),
//        ) == data.label
//    }.let { println(it.toDouble() / test.size) }
}

suspend fun createWineModel(
    train: List<WineDataset>,
    test: List<WineDataset>,
    epoc: Int,
    seed: Int? = null,
): Int = withContext(Dispatchers.Default) {
    val network = DevNetwork.create(
        InputConfig(13),
        listOf(
            LayerConfig(100, ::relu, LayerType.MatMul),
            LayerConfig(30, ::relu, LayerType.MatMul),
            LayerConfig(3, ::sigmoid, LayerType.MatMul),
        ),
        seed?.let { Random(it) } ?: Random,
        0.01,
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
    }.also { println(it.toDouble() / test.size) }
}

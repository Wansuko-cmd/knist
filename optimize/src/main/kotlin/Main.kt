@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.wine.wineDatasets
import kotlinx.coroutines.runBlocking
import network.DevNetwork
import network.InputConfig
import network.LayerConfig
import network.LayerType
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    measureTimeMillis { (0..10).forEach { _ -> createWineModel(epoc = 1000) } }.also { println(it) }
}

fun createWineModel(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = wineDatasets.shuffled().map { it.centering() }.chunked(120)
    val network = DevNetwork.create(
        InputConfig(13),
        listOf(
            LayerConfig(50, ::relu, LayerType.MatMul),
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
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

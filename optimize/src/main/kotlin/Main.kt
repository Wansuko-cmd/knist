@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.wine.WineDataset
import dataset.wine.wineDatasets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import network.DevNetwork
import network.InputConfig
import network.LayerConfig
import network.LayerType
import network.WineNearest
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val (train, test) = wineDatasets.map { it.centering() }.shuffled().chunked(120)
    val nearest = WineNearest(train)
    test.count { data ->
        nearest.expect(data) == data.label
    }.also { println(it.toDouble() / test.size) }
    createWineModel(train, test, 1000, 19)
//    (0..30)
//        .map { async { createWineModel(train = train, test = test, epoc = 1000, seed = it) to it } }
//        .map { it.await().also { println("${it.second} Done") } }
//        .sortedByDescending { it.first }
//        .take(10)
//        .also { println(it.joinToString("\n") { (score, seed) -> "Seed: $seed, Score: ${score.toDouble() / test.size}" }) }
//        .map { it.second }
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
        0.01
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

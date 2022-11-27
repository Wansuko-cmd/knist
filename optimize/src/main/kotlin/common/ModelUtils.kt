package common

import dataset.iris.IrisDataset
import dataset.iris.datasets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

suspend fun createModel(
    train: List<IrisDataset>,
    test: List<IrisDataset>,
    epoc: Int,
    seed: Int? = null,
): Int = withContext(Dispatchers.Default) {
    val network = Network.create(listOf(4, 50, 3), seed?.let { Random(it) } ?: Random)
    (1..epoc).forEach { _ ->
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
    return@withContext test.count { data ->
        network.expect(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ) == data.label
    }
}

suspend fun checkAverage(seed: Int, count: Int, epoc: Int): Double = withContext(Dispatchers.Default) {
    return@withContext (1..count).map {
        val (train, test) = datasets.shuffled().chunked(120)
        async { createModel(train = train, test = test, epoc = epoc, seed = seed) to test.size }
    }
        .map { it.await() }
        .fold(0 to 0) { acc, (correct, size) -> acc.first + correct to acc.second + size }
        .let { it.first.toDouble() / it.second.toDouble() }
}

suspend fun searchGoodSeed(from: Int, to: Int, epoc: Int): List<Int> = withContext(Dispatchers.Default) {
    val (train, test) = datasets.shuffled().chunked(120)
    (from..to)
        .map { async { createModel(train = train, test = test, epoc = epoc, seed = it) to it } }
        .map { it.await() }
        .sortedBy { it.first }
        .take(10)
        .also { println(it.joinToString("\n") {  (score, seed) -> "Seed: $seed, Score: ${score.toDouble() / test.size}" }) }
        .map { it.second }
}

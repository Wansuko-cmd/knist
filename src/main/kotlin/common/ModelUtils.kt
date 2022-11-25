package common

import dataset.iris.IrisDataset
import dataset.iris.datasets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import layer.Layer
import kotlin.random.Random

suspend fun createModel(
    train: List<IrisDataset>,
    test: List<IrisDataset>,
    epoc: Int,
    seed: Int? = null,
): Int = withContext(Dispatchers.Default) {
    val model = (1..epoc).fold(
        Layer.create(
            input = 4,
            center = listOf(7, 8),
            output = 3,
            rate = 0.01,
            random = seed?.let { Random(it) } ?: Random,
        ),
    ) { model, index ->
        println("epoc: $index")
        train.fold(model) { acc, element ->
            acc.train(
                input = listOf(
                    element.petalLength,
                    element.petalWidth,
                    element.sepalLength,
                    element.sepalWidth,
                ),
                label = element.label,
            )
        }
    }
    return@withContext test.count { data ->
        model.forward(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ).also { println("$it, except: ${it.maxIndex()} label: ${data.label}") }.maxIndex() == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

suspend fun checkAverage(seed: Int, count: Int, epoc: Int) = withContext(Dispatchers.Default) {
    return@withContext (1..count).map {
        val (train, test) = datasets.shuffled().chunked(120)
        async { createModel(train = train, test = test, epoc = epoc, seed = seed) to test.size }
    }
        .map { it.await() }
        .fold(0 to 0) { acc, (correct, size) -> acc.first + correct to acc.second + size }
        .let { it.first.toDouble() / it.second.toDouble() }.also { println("Average: $it") }
}

suspend fun searchGoodSeed(from: Int, to: Int, epoc: Int): Int = withContext(Dispatchers.Default) {
    val (train, test) = datasets.shuffled().chunked(120)
    (from..to)
        .map { async { createModel(train = train, test = test, epoc = epoc, seed = it) to it } }
        .maxBy { it.await().first }.await().second
        .also { println("Best Seed: $it") }
}

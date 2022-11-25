import dataset.IrisDataset
import dataset.datasets
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import layer.Layer

const val SEED = 12
val random = Random(SEED)

fun main(): Unit = runBlocking {
    val (train, test) = datasets.shuffled().chunked(120)

    val a = checkAverage(12)
    val b = checkAverage(32)
    println("a: $a, b: $b")
//    createModel(train, test, 32)
//    (30..100)
//        .map { async { createModel(train, test, it) to it } }
//        .maxBy { it.await().first }
//        .also { println(it.await().second) }
}

suspend fun checkAverage(seed: Int) = withContext(Dispatchers.Default) {
    return@withContext (1..20).map {
        val (train, test) = datasets.shuffled().chunked(120)
        async { createModel(train, test, seed) to test.size }
    }
        .map { it.await() }
        .fold(0 to 0) { acc, (correct, size) -> acc.first + correct to acc.second + size }
        .let { it.first.toDouble() / it.second.toDouble() }.also { println("Average: $it") }
}

suspend fun createModel(
    train: List<IrisDataset>,
    test: List<IrisDataset>,
    seed: Int,
): Int = withContext(Dispatchers.Default){

    val model = (1..50).fold(
        Layer.create(input = 4, center = listOf(5, 8), output = 3, rate = 0.01, random = Random(seed)),
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
        ) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}
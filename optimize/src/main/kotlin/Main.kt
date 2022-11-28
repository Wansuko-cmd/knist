
import common.checkAverage
import dataset.mnist.MnistDataset
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
//    println("Score: ${checkAverage(0, 100, 500)}")
//    println("Score: ${checkAverage(0, 100, 50)}")
    val (train, test) = MnistDataset.read().chunked(2000)
    (1..1000).map {
        async {
            val network =
                Network.create(listOf(train.first().imageSize * train.first().imageSize, 32, 64, 512, 10), Random, 0.03)
            (1..2).forEach { epoc ->
                println("epoc: $epoc")
                train.forEach { data ->
                    network.train(
                        input = data.pixels,
                        label = data.label,
                    )
                }
            }
            test.count { data ->
                network.expect(
                    input = data.pixels,
                ) == data.label
            } to it
        }
    }
        .map { it.await() }
        .sortedBy { it.first }
        .take(10)
        .also { println(it.joinToString("\n") {  (score, seed) -> "Seed: $seed, Score: ${score.toDouble() / test.size}" }) }
        .map { it.second }
}

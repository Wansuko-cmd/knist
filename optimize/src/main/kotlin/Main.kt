
import common.checkAverage
import dataset.mnist.MnistDataset
import kotlin.random.Random
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
//    println("Score: ${checkAverage(0, 100, 200)}")
//    println("Score: ${checkAverage(0, 100, 50)}")
    val (train, test) = MnistDataset.read().chunked(20000)
    val network =
        Network.create(listOf(train.first().imageSize * train.first().imageSize, 392, 98, 49, 10), Random, 0.01)
    (1..50).forEach { epoc ->
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
    }.let { println("${it.toDouble() / test.size}") }
}

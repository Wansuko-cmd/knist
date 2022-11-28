
import common.checkAverage
import dataset.mnist.MnistDataset
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
//    println("Score: ${checkAverage(0, 100, 500)}")
//    println("Score: ${checkAverage(0, 100, 50)}")
    val (train, test) = MnistDataset.read().chunked(20000)
    val network = Network.create(listOf(train.first().imageSize * train.first().imageSize, 32, 64, 512, 10), Random, 0.03)
    (1..5).forEach { epoc ->
        println("epoc: $epoc")
        measureTimeMillis {
            train.forEach { data ->
                network.train(
                    input = data.pixels,
                    label = data.label,
                )
            }
        }.let { println(it) }
    }
    test.count { data ->
        network.expect(
            input = data.pixels,
        ) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

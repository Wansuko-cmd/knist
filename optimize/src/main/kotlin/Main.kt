@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.iris.datasets
import kotlinx.coroutines.runBlocking
import network.DevNetwork
import network.LayerConfig
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val network = DevNetwork.create(
        listOf(
            LayerConfig(4, ::relu),
            LayerConfig(50, ::relu),
            LayerConfig(3, ::sigmoid),
        ),
        Random(1652),
        0.01,
    )
    val (train, test) = datasets.shuffled().chunked(120)
    (1..1000).forEach { _ ->
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
    test.count { data ->
        network.expect(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ) == data.label
    }.let { println(it.toDouble() / test.size) }

//    val input = listOf(
//        listOf(0, 1, 2, 3),
//        listOf(1, 2, 3, 0),
//        listOf(2, 3, 0, 1),
//        listOf(3, 0, 1, 2),
//    ).map { it.map { it.toDouble() } }
//    val kernel = listOf(
//        listOf(0, 1, 2),
//        listOf(1, 2, 0),
//        listOf(2, 0, 1),
//    ).map { it.map { it.toDouble() } }
//    println(input.conv(kernel))

//    searchGoodSeed(0, 10000, 1000)
//        .map { checkAverage(it, 50, 1000) to it }
//        .sortedByDescending { it.first }
//        .forEach { (score, seed) -> println("Seed: $seed, Score: $score") }
//    println(measureTimeMillis { println("Score: ${checkAverage(0, 100, 10000)}") })
//    println("Score: ${checkAverage(0, 100, 50)}")
//    val (train, test) = MnistDataset.read().chunked(20000)
//    val network =
//        network.Network.create(listOf(train.first().imageSize * train.first().imageSize, 392, 98, 49, 10), Random, 0.01)
//    (1..50).forEach { epoc ->
//        println("epoc: $epoc")
//        train.forEach { data ->
//            network.train(
//                input = data.pixels,
//                label = data.label,
//            )
//        }
//    }
//    test.count { data ->
//        network.expect(
//            input = data.pixels,
//        ) == data.label
//    }.let { println("${it.toDouble() / test.size}") }
}

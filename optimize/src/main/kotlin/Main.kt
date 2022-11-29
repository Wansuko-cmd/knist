@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.iris.datasets
import dataset.mnist.MnistDataset
import kotlinx.coroutines.runBlocking
import network.DevNetwork
import network.LayerConfig
import network.LayerType
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val network = DevNetwork.create(
        listOf(
            LayerConfig(4, ::relu, LayerType.MatMul),
            LayerConfig(50, ::relu, LayerType.MatMul),
            LayerConfig(3, ::sigmoid, LayerType.MatMul),
        ),
        Random(1652),
        0.01
    )
    val (train, test) = datasets.shuffled().chunked(120)
    (1..1000).forEach { epoc ->
        println("epoc: $epoc")
        train.forEach { data ->
            network.train(
                input = listOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalWidth,
                    data.sepalLength,
                ),
                label = data.label
            )
        }
    }
    test.count { data ->
        network.expect(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalWidth,
                data.sepalLength,
            ),
        ) == data.label
    }.let { println(it.toDouble() / test.size) }
//    val (train, test) = MnistDataset.read().chunked(200)
//    val network = DevNetwork.create(
//        listOf(
//            LayerConfig(1, ::relu, LayerType.Conv),
//            LayerConfig(32, ::relu, LayerType.Conv),
//            LayerConfig(30, ::relu, LayerType.MatMul),
//            LayerConfig(10, ::sigmoid, LayerType.MatMul),
//        ),
//        Random(1652),
//        0.01,
//    )
//    (1..5).forEach { epoc ->
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

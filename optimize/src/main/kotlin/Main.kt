@file:Suppress("DuplicatedCode")

import common.relu
import common.sigmoid
import dataset.mnist.MnistDataset
import kotlinx.coroutines.runBlocking
import network.DevNetwork
import network.InputConfig
import network.LayerConfig
import network.LayerType
import kotlin.random.Random

fun main(): Unit = runBlocking {
//    val network = DevNetwork.create<Nothing>(
//        InputConfig(4),
//        listOf(
//            LayerConfig(50, ::relu, LayerType.MatMul),
//            LayerConfig(3, ::sigmoid, LayerType.MatMul),
//        ),
//        Random(1652),
//        0.01
//    )
//    val (train, test) = datasets.shuffled().chunked(120)
//    (1..1000).forEach { epoc ->
//        println("epoc: $epoc")
//        train.forEach { data ->
//            network.train(
//                input = listOf(
//                    data.petalLength,
//                    data.petalWidth,
//                    data.sepalWidth,
//                    data.sepalLength,
//                ),
//                label = data.label
//            )
//        }
//    }
//    test.count { data ->
//        network.expect(
//            input = listOf(
//                data.petalLength,
//                data.petalWidth,
//                data.sepalWidth,
//                data.sepalLength,
//            ),
//        ) == data.label
//    }.let { println(it.toDouble() / test.size) }
    val (train, test) = MnistDataset.read().chunked(2000)
    val network = DevNetwork.create(
        InputConfig(size = 1),
        listOf(
            LayerConfig(size = 32, activationFunction = ::relu, type = LayerType.Conv),
            LayerConfig(size = 64, activationFunction = ::relu, type = LayerType.Conv),
            LayerConfig(size = 30, activationFunction = ::relu, type = LayerType.MatMul),
            LayerConfig(size = 10, activationFunction = ::sigmoid, type = LayerType.MatMul),
        ),
        random = Random(1652),
        rate = 0.01,
    )
    (1..5).forEach { epoc ->
        println("epoc: $epoc")
        train.shuffled().take(1000).forEach { data ->
            network.trains(
                input = listOf(data.pixels.chunked(train.first().imageSize)),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expects(
            input = listOf(data.pixels.chunked(train.first().imageSize)),
        ) == data.label
    }.let { println(it.toDouble() / test.size) }
}

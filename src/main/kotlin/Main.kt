import common.checkAverage
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    measureTimeMillis { checkAverage(12, 10, 50) }.also { println(it) }
//    val (train, test) = MnistDataset.read().chunked(5000)
//    val model = (1..10).fold(
//        Layer.create(
//            input = train.first().imageSize * train.first().imageSize,
//            center = listOf(32),
//            output = 10,
//            rate = 0.03
//        ),
//    ) { model, index ->
//        println("epoc: $index")
//        train.fold(model) { acc, element ->
//            println(element)
//            acc.train(
//                input = element.pixels,
//                label = element.label,
//            )
//        }
//    }
//    train.count { data ->
//        model.forward(
//            input = data.pixels,
//        ).also { println("$it, except: ${it.maxIndex()} label: ${data.label}") }.maxIndex() == data.label
//    }.also { println(it.toDouble() / train.size.toDouble()) }
}

/**
 *
 */

/**
 * Seed値 例: 12, 32
 */

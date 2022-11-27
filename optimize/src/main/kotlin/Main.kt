
import common.checkAverage
import common.searchGoodSeed
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    println("Score: ${checkAverage(0, 100, 1000)}")
//    searchGoodSeed(10000, 10050, 100)
//        .map { checkAverage(it, 30, 100) to it }
//        .maxBy { it.first }
//        .let { (score, seed) -> println("Best Seed: $seed, Best Score: $score") }
}

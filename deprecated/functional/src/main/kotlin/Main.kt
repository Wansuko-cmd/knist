import common.checkAverage
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    measureTimeMillis { checkAverage(12, 30, 100) }.also { println(it) }
}

/**
 *
 */

/**
 * Seed値 例: 12, 32
 */

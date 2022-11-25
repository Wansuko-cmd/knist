import common.checkAverage
import common.searchGoodSeed
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    searchGoodSeed(from = 200, to = 300, epoc = 50).also(::println)
}

/**
 * Seed値 例: 12, 32
 */
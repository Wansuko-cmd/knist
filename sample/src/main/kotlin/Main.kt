import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    // 5m 59.059936542s
    measureTime {
        createMnistModel(10, 3)
    }.also(::println)
}

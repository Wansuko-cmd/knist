import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    measureTime {
        createMnistModel(10, 3)
    }.also(::println)
}

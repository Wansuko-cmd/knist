import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    measureTime {
        createMnistModel(1, 3)
    }.also(::println)
}

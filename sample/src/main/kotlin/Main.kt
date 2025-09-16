import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    measureTime {
        createMnistModel(3, 3)
    }.also(::println)
}

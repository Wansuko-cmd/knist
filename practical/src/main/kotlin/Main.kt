
import dataset.mnist.createMnistModel
import dataset.wine.createWineModel
import kotlin.system.measureTimeMillis

fun main() {
    measureTimeMillis { (0..10).forEach { _ -> createMnistModel(epoc = 10, 1) } }.also { println(it) }
}

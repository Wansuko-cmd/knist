
import dataset.mnist.createMnistModel
import dataset.mnist.createMnistModel0d
import dataset.wine.createWineModel
import kotlin.system.measureTimeMillis

fun main() {
    createMnistModel(epoc = 5)
//    measureTimeMillis { (0..10).forEach { _ -> createWineModel(epoc = 1000, 1) } }.also { println(it) }
}

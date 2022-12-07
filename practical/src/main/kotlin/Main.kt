
import dataset.wine.createWineModel
import kotlin.system.measureTimeMillis

fun main() {
    measureTimeMillis { (0..10).forEach { _ -> createWineModel(epoc = 1000) } }.also { println(it) }
}

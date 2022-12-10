
import dataset.mnist.createMnistModel
import dataset.mnist.createMnistModel0d
import dataset.wine.createWineModel
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorMask
import jdk.incubator.vector.VectorOperators
import layers.layer1d.conv1d
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun main() {
//    val input = doubleArrayOf(2.0, 1.0, 0.0, -1.0)
//    val kernel = doubleArrayOf(1.0, 2.0)
//    val output = DoubleArray(input.size - kernel.size + 1) { 0.0 }
//    input.conv1d(kernel, output)
//    measureNanoTime { (1..10000).forEach { input.conv1d(kernel, output) } }.let { println(it) }
    createMnistModel0d(epoc = 30)
//    measureTimeMillis { (0..10).forEach { _ -> createWineModel(epoc = 1000, 1) } }.also { println(it) }
}

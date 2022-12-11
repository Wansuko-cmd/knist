
import dataset.mnist.createMnistModel
import dataset.mnist.createMnistModel0d
import dataset.signal.createSignalModel
import dataset.signal.createSignalModel0d
import dataset.signal.signalDatasets
import dataset.wine.createWineModel
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorMask
import jdk.incubator.vector.VectorOperators
import layers.layer1d.conv1d
import layers.layer1d.deConv1d
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun main() {
//    val input = doubleArrayOf(2.0, 1.0, 0.0, -1.0)
//    val kernel = doubleArrayOf(1.0, 2.0)
//    val output = DoubleArray(5) { 0.0 }
//    input.deConv1d(kernel, output)
//    println(output.map { it })
    createMnistModel(2)
}

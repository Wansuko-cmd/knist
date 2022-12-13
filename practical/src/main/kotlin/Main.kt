
import common.conv2d
import common.innerProduct
import dataset.mnist.createMnistModel0d
import dataset.mnist.createMnistModel1d
import dataset.mnist.createMnistModel2d

fun main() {
    val input = Array(5) { i -> DoubleArray(5) { j -> i * 5.0 + j + 1 } }
    val kernel = Array(3) { i -> DoubleArray(3) { j -> i * 3.0 + j + 1 } }
    val output = Array(3) { DoubleArray(3) }
    input.conv2d(kernel, output)
    println(output.map { it.toList() }).let {  }
//    createIrisModel(1000)
//    createMnistModel1d(3, 1)
}

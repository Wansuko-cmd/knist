@file:Suppress("NOTHING_TO_INLINE")

package common

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies

val sp: VectorSpecies<Double> = DoubleVector.SPECIES_PREFERRED

inline fun DoubleArray.innerProduct(other: DoubleArray): Double {
    var sum = 0.0
    var index = 0
    while (index < sp.loopBound(this.size)) {
        val left = DoubleVector.fromArray(sp, this, index)
        val right = DoubleVector.fromArray(sp, other, index)
        sum += left.mul(right).reduceLanes(VectorOperators.ADD)
        index += sp.length()
    }
    while (index < this.size) {
        sum += this[index] * other[index]
        index++
    }
    return sum
}

inline fun DoubleArray.conv1d(
    kernel: DoubleArray,
    output: DoubleArray,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        var index = 0
        while (index < sp.loopBound(kernel.size)) {
            val i = DoubleVector.fromArray(sp, this, outputIndex + index)
            val k = DoubleVector.fromArray(sp, kernel, index)
            sum += i.mul(k).reduceLanes(VectorOperators.ADD)
            index += sp.length()
        }
        while (index < kernel.size) {
            sum += this[outputIndex + index] * kernel[index]
            index++
        }
        output[outputIndex] += sum
    }
}

inline fun DoubleArray.deConv1d(
    kernel: DoubleArray,
    output: DoubleArray,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(kernel.size - 1) { 0.0 },
        *this.toTypedArray().toDoubleArray(),
        *DoubleArray(kernel.size - 1) { 0.0 },
    )
    for (outputIndex in output.indices) {
        var sum = 0.0
        var index = 0
        while (index < sp.loopBound(kernel.size)) {
            val i = DoubleVector.fromArray(sp, resizedInput, outputIndex + index)
            val k = DoubleVector.fromArray(sp, kernel, index)
            sum += i.mul(k).reduceLanes(VectorOperators.ADD)
            index += sp.length()
        }
        while (index < kernel.size) {
            sum += resizedInput[outputIndex + index] * kernel[index]
            index++
        }
        output[outputIndex] += sum
    }
}

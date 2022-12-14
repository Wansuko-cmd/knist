@file:Suppress("NOTHING_TO_INLINE")

package common.iotype

import common.sp
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators

inline fun IOType0d.innerProduct(other: IOType0d, otherFrom: Int): Double {
    var sum = 0.0
    var index = 0
    while (index < sp.loopBound(this.size)) {
        val left = DoubleVector.fromArray(sp, this.inner, index)
        val right = DoubleVector.fromArray(sp, other.inner, index + otherFrom)
        sum += left.mul(right).reduceLanes(VectorOperators.ADD)
        index += sp.length()
    }
    while (index < this.size) {
        sum += this[index] * other[index + otherFrom]
        index++
    }
    return sum
}

inline fun IOType1d.innerProduct(
    other: IOType1d,
    otherRow: Int,
    otherColumn: Int,
): Double {
    var sum = 0.0
    for (row in this.indices) {
        sum += this[row].innerProduct(other[row + otherRow], otherColumn)
    }
    return sum
}

inline fun IOType0d.conv1d(
    kernel: IOType0d,
    output: IOType0d,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(padding) { 0.0 },
        *this.inner.toTypedArray().toDoubleArray(),
        *DoubleArray(padding) { 0.0 },
    )
    for (outputTime in output.indices step stride) {
        output[outputTime] += kernel.innerProduct(IOType0d(resizedInput), outputTime)
    }
}

inline fun IOType0d.deConv1d(
    kernel: IOType0d,
    output: IOType0d,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(kernel.size - padding - 1) { 0.0 },
        *this
            .inner
            .fold(doubleArrayOf()) { acc, d -> acc + d + DoubleArray(stride - 1) }
            .drop(stride - 1)
            .toTypedArray()
            .toDoubleArray(),
        *DoubleArray(kernel.size - padding - 1) { 0.0 },
    )
    IOType0d(resizedInput).conv1d(kernel, output)
}

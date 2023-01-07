@file:Suppress("NOTHING_TO_INLINE")

package common

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies

val sp: VectorSpecies<Double> = DoubleVector.SPECIES_PREFERRED

inline fun DoubleArray.innerProduct(other: DoubleArray, otherFrom: Int): Double {
    var sum = 0.0
    var index = 0
    while (index < sp.loopBound(this.size)) {
        val left = DoubleVector.fromArray(sp, this, index)
        val right = DoubleVector.fromArray(sp, other, index + otherFrom)
        sum += left.mul(right).reduceLanes(VectorOperators.ADD)
        index += sp.length()
    }
    while (index < this.size) {
        sum += this[index] * other[index + otherFrom]
        index++
    }
    return sum
}

inline fun Array<DoubleArray>.innerProduct(
    other: Array<DoubleArray>,
    otherRow: Int,
    otherColumn: Int,
): Double {
    var sum = 0.0
    for (row in this.indices) {
        sum += this[row].innerProduct(other[row + otherRow], otherColumn)
    }
    return sum
}

inline fun DoubleArray.conv1d(
    kernel: DoubleArray,
    output: DoubleArray,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(padding) { 0.0 },
        *this.toTypedArray().toDoubleArray(),
        *DoubleArray(padding) { 0.0 },
    )
    for (outputTime in output.indices step stride) {
        output[outputTime] += kernel.innerProduct(resizedInput, outputTime)
    }
}

inline fun Array<DoubleArray>.conv2d(
    kernel: Array<DoubleArray>,
    output: Array<DoubleArray>,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = arrayOf(
        *Array(padding) { DoubleArray(this.size + padding * 2) },
        *this.map {
            doubleArrayOf(
                *DoubleArray(padding) { 0.0 },
                *it.toTypedArray().toDoubleArray(),
                *DoubleArray(padding) { 0.0 },
            )
        }.toTypedArray(),
        *Array(padding) { DoubleArray(this.size + padding * 2) },
    )
    for (row in output.indices step stride) {
        for (column in output[row].indices step stride) {
            output[row][column] += kernel.innerProduct(resizedInput, row, column)
        }
    }
}

inline fun DoubleArray.deConv1d(
    kernel: DoubleArray,
    output: DoubleArray,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(kernel.size - padding - 1) { 0.0 },
        *this
            .fold(doubleArrayOf()) { acc, d -> acc + d + DoubleArray(stride - 1) }
            .drop(stride - 1)
            .toTypedArray()
            .toDoubleArray(),
        *DoubleArray(kernel.size - padding - 1) { 0.0 },
    )
    resizedInput.conv1d(kernel, output)
}

inline fun Array<DoubleArray>.deConv2d(
    kernel: Array<DoubleArray>,
    output: Array<DoubleArray>,
    padding: Int = 0,
    stride: Int = 1,
) {
    val resizedInput = arrayOf(
        *Array(kernel.size - 1) { DoubleArray(this.size + 2 * kernel.size - 2) },
        *this.map {
            doubleArrayOf(
                *DoubleArray(kernel.size - 1) { 0.0 },
                *it.toTypedArray().toDoubleArray(),
                *DoubleArray(kernel.size - 1) { 0.0 },
            )
        }.toTypedArray(),
        *Array(kernel.size - 1) { DoubleArray(this.size + 2 * kernel.size - 2) },
    )
    resizedInput.conv2d(kernel, output)
}

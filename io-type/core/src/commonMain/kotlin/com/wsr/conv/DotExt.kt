package com.wsr.conv

infix fun Array<DoubleArray>.dot(other: Array<DoubleArray>): Array<DoubleArray> {
    val result = Array(other.size) { DoubleArray(size) }
    for (f in other.indices) {
        for (i in indices) {
            result[f][i] = this[i] dot other[f]
        }
    }
    return result
}

infix fun DoubleArray.dot(other: DoubleArray): Double {
    var sum = 0.0
    for (i in indices) {
        sum += this[i] * other[i]
    }
    return sum
}

package com.wsr.d1

import com.wsr.IOType

infix fun IOType.D1.dot(other: IOType.D1): Double {
    var sum = 0.0
    for (i in value.indices) {
        sum += this[i] * other[i]
    }
    return sum
}

infix fun IOType.D1.dot(other: IOType.D2) = IOType.d1(size = shape[0]) { i ->
    var sum = 0.0
    for (j in 0 until other.shape[1]) {
        sum += other[i, j]
    }
    this[i] * sum
}

@JvmName("dotToD1s")
infix fun IOType.D1.dot(other: List<IOType.D1>) = List(other.size) { this dot other[it] }

@JvmName("dotToD2s")
infix fun IOType.D1.dot(other: List<IOType.D2>) = List(other.size) { this dot other[it] }

@JvmName("dotToD1s")
infix fun List<IOType.D1>.dot(other: List<IOType.D1>) = List(size) { this[it] dot other[it] }

@JvmName("dotToD2s")
infix fun List<IOType.D1>.dot(other: List<IOType.D2>) = List(size) { this[it] dot other[it] }

package com.wsr.common.d2

import com.wsr.common.IOType

infix fun IOType.D2.dot(other: IOType.D1) = IOType.d1(shape[0]) { i ->
    var sum = 0.0
    for (j in 0 until shape[1]) {
        sum += this[i, j] * other[j]
    }
    sum
}

infix fun IOType.D2.dot(other: IOType.D2) = IOType.d2(shape[0], other.shape[1]) { x, y ->
    var sum = 0.0
    for (i in 0 until shape[1]) {
        sum += this[x, i] * other[i, y]
    }
    sum
}

@JvmName("dotToD1s")
infix fun IOType.D2.dot(other: List<IOType.D1>) = List(other.size) { this dot other[it] }

@JvmName("dotToD2s")
infix fun IOType.D2.dot(other: List<IOType.D2>) = List(other.size) { this dot other[it] }

@JvmName("dotToD1s")
infix fun List<IOType.D2>.dot(other: List<IOType.D1>) = List(size) { this[it] dot other[it] }

@JvmName("dotToD2s")
infix fun List<IOType.D2>.dot(other: List<IOType.D2>) = List(size) { this[it] dot other[it] }

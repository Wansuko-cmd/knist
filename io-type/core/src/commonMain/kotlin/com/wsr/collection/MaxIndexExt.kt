package com.wsr.collection

import com.wsr.IOType

fun IOType.D1.maxIndex(): Int {
    if (value.isEmpty()) throw IllegalStateException("IOType.D1 is empty")
    var index = 0
    var max = Float.MIN_VALUE
    for (i in value.indices) {
        if (max < this[i]) {
            index = i
            max = this[i]
        }
    }
    return index
}

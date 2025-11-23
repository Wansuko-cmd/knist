package com.wsr.core.collection.index

import com.wsr.core.IOType
import com.wsr.core.get

fun IOType.D1.maxIndex(): Int {
    var index = 0
    var max = Float.MIN_VALUE
    for (i in 0 until shape[0]) {
        if (max < this[i]) {
            index = i
            max = this[i]
        }
    }
    return index
}

package com.wsr.conv

import com.wsr.IOType

fun IOType.D3.toFilter(): Array<DoubleArray> {
    val filterCount = shape[0]
    val channels = shape[1]
    val kernel = shape[2]

    return Array(filterCount) { f ->
        DoubleArray(channels * kernel) { i ->
            val c = i / kernel
            val k = i % kernel
            this[f, c, k]
        }
    }
}

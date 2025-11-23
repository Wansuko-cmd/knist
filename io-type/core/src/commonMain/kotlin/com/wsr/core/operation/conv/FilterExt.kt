package com.wsr.core.operation.conv

import com.wsr.core.IOType
import com.wsr.core.get

fun IOType.D3.toFilter(): Array<FloatArray> {
    val filterCount = shape[0]
    val channels = shape[1]
    val kernel = shape[2]

    return Array(filterCount) { f ->
        FloatArray(channels * kernel) { i ->
            val c = i / kernel
            val k = i % kernel
            this[f, c, k]
        }
    }
}

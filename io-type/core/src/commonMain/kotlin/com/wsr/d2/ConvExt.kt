package com.wsr.d2

import com.wsr.IOType
import com.wsr.d1.convD1
import com.wsr.d1.deConvD1
import com.wsr.d1.toD2

fun IOType.D2.convD1(filter: IOType.D2, stride: Int = 1, padding: Int = 0): IOType.D2 {
    val channel = shape[0]
    return List(channel) { c ->
        this[c].convD1(
            filter = filter[c],
            stride = stride,
            padding = padding,
        )
    }.toD2()
}

fun IOType.D2.deConvD1(filter: IOType.D2, stride: Int = 1, padding: Int = 0): IOType.D2 {
    val channel = shape[0]
    return List(channel) { c ->
        this[c].deConvD1(
            filter = filter[c],
            stride = stride,
            padding = padding,
        )
    }.toD2()
}

package com.wsr.d2

import com.wsr.IOType
import com.wsr.d1.convD1
import com.wsr.d1.plus

fun IOType.D2.convD1(filter: IOType.D2, stride: Int = 1, padding: Int = 0): IOType.D1 {
    val channel = shape[0]
    return List(channel) { c ->
        this[c].convD1(
            filter = filter[c],
            stride = stride,
            padding = padding,
        )
    }
        .reduce { acc, d1 -> acc + d1 }
}

//fun IOType.D2.deConvD1(filter: IOType.D2): IOType.D1 {
//    val channel = shape[0]
//    val outputSize = shape[1] - filter.shape[1] + 1
//    val kernel = filter.shape[1]
//    return IOType.d1(channel) { x ->
//        var sum = 0.0
//        for (f in 0 until outputSize) {
//            for (k in 0 until kernel) {
//                sum += this[]
//            }
//        }
//
//        for (c in 0 until channel) {
//            for (k in 0 until kernel) {
//                sum += this[c, x + k] * filter[c, k]
//            }
//        }
//        sum
//    }
//    TODO()
//}

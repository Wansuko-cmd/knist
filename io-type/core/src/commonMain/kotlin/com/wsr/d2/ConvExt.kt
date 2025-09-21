package com.wsr.d2

import com.wsr.IOType

fun IOType.D2.convD1(filter: IOType.D2): IOType.D1 {
    val outputSize = shape[1] - filter.shape[1] + 1
    val channel = shape[0]
    val kernel = filter.shape[1]
    return IOType.d1(outputSize) { x ->
        var sum = 0.0
        for (c in 0 until channel) {
            for (k in 0 until kernel) {
                sum += this[c, x + k] * filter[c, k]
            }
        }
        sum
    }
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

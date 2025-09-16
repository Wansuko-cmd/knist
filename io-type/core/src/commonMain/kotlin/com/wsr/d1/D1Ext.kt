package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.average(): IOType.D1 = IOType.d1(first().shape) { x -> average(x) }

fun List<IOType.D1>.average(x: Int): Double = sumOf { it[x] } / size

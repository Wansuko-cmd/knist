package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.average() = IOType.d1(first().shape) { x -> average(x) }

fun List<IOType.D1>.average(x: Int) = map { it[x] }.average()

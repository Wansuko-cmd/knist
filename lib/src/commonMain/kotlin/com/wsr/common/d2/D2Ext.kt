package com.wsr.common.d2

import com.wsr.common.IOType

fun List<IOType.D2>.average() = IOType.d2(first().shape) { x, y -> average(x, y) }

fun List<IOType.D2>.average(x: Int, y: Int) = map { it[x, y] }.average()

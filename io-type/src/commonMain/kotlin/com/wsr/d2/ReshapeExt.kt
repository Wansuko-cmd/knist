package com.wsr.d2

import com.wsr.IOType

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun List<IOType.D2>.transpose() = this.map { it.transpose() }

package com.wsr.power

import com.wsr.IOType
import com.wsr.d0
import com.wsr.d1
import com.wsr.d2
import com.wsr.d3
import com.wsr.get
import kotlin.math.pow

fun IOType.D0.pow(n: Int) = IOType.d0(get().pow(n))

fun IOType.D1.pow(n: Int): IOType.D1 = IOType.d1(shape) { this[it].pow(n) }

fun IOType.D2.pow(n: Int): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j].pow(n) }

fun IOType.D3.pow(n: Int): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k].pow(n) }

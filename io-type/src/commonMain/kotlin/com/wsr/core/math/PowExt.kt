package com.wsr.core.math

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.get
import kotlin.math.pow

fun IOType.D0.pow(n: Int) = IOType.d0(get().pow(n))

fun IOType.D1.pow(n: Int): IOType.D1 = IOType.d1(shape) { this[it].pow(n) }

fun IOType.D2.pow(n: Int): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j].pow(n) }

fun IOType.D3.pow(n: Int): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k].pow(n) }

fun IOType.D4.pow(n: Int): IOType.D4 = IOType.d4(shape) { i, j, k, l -> this[i, j, k, l].pow(n) }

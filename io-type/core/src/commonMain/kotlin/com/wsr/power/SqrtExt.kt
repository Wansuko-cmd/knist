package com.wsr.power

import com.wsr.IOType
import com.wsr.d1
import com.wsr.d2
import com.wsr.d3
import com.wsr.get

fun IOType.D1.sqrt(e: Float = 1e-7f): IOType.D1 = IOType.d1(shape) { kotlin.math.sqrt(this[it] + e) }

fun IOType.D2.sqrt(e: Float = 1e-7f): IOType.D2 = IOType.d2(shape) { i, j -> kotlin.math.sqrt(this[i, j] + e) }

fun IOType.D3.sqrt(e: Float = 1e-7f): IOType.D3 = IOType.d3(shape) { i, j, k -> kotlin.math.sqrt(this[i, j, k] + e) }

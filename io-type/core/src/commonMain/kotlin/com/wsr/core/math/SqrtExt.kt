package com.wsr.core.math

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
fun IOType.D0.sqrt(e: Float = 1e-7f): IOType.D0 = IOType.d0(kotlin.math.sqrt(this.get() + e))

fun IOType.D1.sqrt(e: Float = 1e-7f): IOType.D1 = IOType.d1(shape) { kotlin.math.sqrt(this[it] + e) }

fun IOType.D2.sqrt(e: Float = 1e-7f): IOType.D2 = IOType.d2(shape) { i, j -> kotlin.math.sqrt(this[i, j] + e) }

fun IOType.D3.sqrt(e: Float = 1e-7f): IOType.D3 = IOType.d3(shape) { i, j, k -> kotlin.math.sqrt(this[i, j, k] + e) }

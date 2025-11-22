package com.wsr.power

import com.wsr.IOType

fun IOType.D1.ln(e: Float): IOType.D1 = IOType.d1(shape) { kotlin.math.ln(this[it] + e) }

fun IOType.D2.ln(e: Float): IOType.D2 = IOType.d2(shape) { i, j -> kotlin.math.ln(this[i, j] + e) }

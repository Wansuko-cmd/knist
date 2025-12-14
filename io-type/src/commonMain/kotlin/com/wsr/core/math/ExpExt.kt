package com.wsr.core.math

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun IOType.D1.exp() = IOType.d1(shape) { kotlin.math.exp(this[it]) }

fun IOType.D2.exp() = IOType.d2(shape) { i, j -> kotlin.math.exp(this[i, j]) }

fun IOType.D3.exp() = IOType.d3(shape) { i, j, k -> kotlin.math.exp(this[i, j, k]) }

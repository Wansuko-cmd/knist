package com.wsr.core.reshape.reshape

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3

fun IOType.D2.reshapeToD3(i: Int, j: Int, k: Int) = reshapeToD3(shape = listOf(i, j, k))

fun IOType.D2.reshapeToD3(shape: List<Int>) = IOType.d3(shape = shape, value = value)

fun IOType.D3.reshapeToD2(i: Int, j: Int) = IOType.d2(shape = listOf(i, j), value = value)

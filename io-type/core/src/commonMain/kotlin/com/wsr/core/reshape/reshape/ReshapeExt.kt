package com.wsr.core.reshape.reshape

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4

fun IOType.D2.reshapeToD3(i: Int, j: Int, k: Int) = reshapeToD3(shape = listOf(i, j, k))

fun IOType.D2.reshapeToD3(shape: List<Int>) = IOType.d3(shape = shape, value = value)

fun IOType.D2.reshapeToD4(i: Int, j: Int, k: Int, l: Int) = IOType.d4(listOf(i, j, k, l), value = value)

fun IOType.D3.reshapeToD2(i: Int, j: Int) = IOType.d2(shape = listOf(i, j), value = value)

fun IOType.D4.reshapeToD2(i: Int, j: Int) = IOType.d2(shape = listOf(i, j), value = value)

fun IOType.D4.reshapeToD3(i: Int, j: Int, k: Int) = IOType.d3(shape = listOf(i, j, k), value = value)

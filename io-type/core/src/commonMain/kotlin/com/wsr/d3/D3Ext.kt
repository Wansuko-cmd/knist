package com.wsr.d3

import com.wsr.IOType
import kotlin.collections.average

fun List<IOType.D3>.average() = IOType.d3(first().shape) { x, y, z -> average(x, y, z) }

fun List<IOType.D3>.average(x: Int, y: Int, z: Int) = map { it[x, y, z] }.average()

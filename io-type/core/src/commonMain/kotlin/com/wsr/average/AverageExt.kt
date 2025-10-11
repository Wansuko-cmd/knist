package com.wsr.average

import com.wsr.IOType

fun IOType.D1.average(): Double = value.average()

fun List<IOType.D1>.average(): IOType.D1 = IOType.d1(first().shape) { x -> average(x) }

fun List<IOType.D1>.average(i: Int): Double = sumOf { it[i] } / size

fun IOType.D2.average() = IOType.d1(shape[0]) { get(it).average() }

fun List<IOType.D2>.average() = IOType.d2(first().shape) { x, y -> average(x, y) }

fun List<IOType.D2>.average(i: Int, j: Int) = map { it[i, j] }.average()

fun IOType.D3.average() = IOType.d2(shape[0], shape[1]) { x, y -> get(x, y).average() }

fun List<IOType.D3>.average() = IOType.d3(first().shape) { x, y, z -> average(x, y, z) }

fun List<IOType.D3>.average(i: Int, j: Int, k: Int) = map { it[i, j, k] }.average()

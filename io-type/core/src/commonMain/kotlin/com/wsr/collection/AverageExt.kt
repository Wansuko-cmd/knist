package com.wsr.collection

import com.wsr.IOType
import com.wsr.operator.div
import com.wsr.operator.plus

fun IOType.D1.average(): Double = value.average()

@JvmName("averageToD1s")
fun List<IOType.D1>.average(): List<Double> = map { it.average() }

fun IOType.D2.average(): IOType.D1 = IOType.d1(shape[0]) { get(it).average() }

@JvmName("averageToD2s")
fun List<IOType.D2>.average(): List<IOType.D1> = map { it.average() }

fun IOType.D3.average(): IOType.D2 = IOType.d2(shape[0], shape[1]) { x, y -> get(x, y).average() }

@JvmName("averageToD3s")
fun List<IOType.D3>.average(): List<IOType.D2> = map { it.average() }

/**
 * batch average
 */
fun List<IOType.D1>.batchAverage(): IOType.D1 = reduce { acc, d1 -> acc + d1 } / size.toDouble()

fun List<IOType.D2>.batchAverage(): IOType.D2 = reduce { acc, d2 -> acc + d2 } / size.toDouble()

fun List<IOType.D3>.batchAverage(): IOType.D3 = reduce { acc, d3 -> acc + d3 } / size.toDouble()

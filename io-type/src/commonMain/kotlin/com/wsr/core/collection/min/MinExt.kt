package com.wsr.core.collection.min

import com.wsr.core.IOType
import com.wsr.core.collection.reduce.reduce

fun IOType.D1.min() = value.toFloatArray().min()

fun IOType.D2.min() = value.toFloatArray().min()

fun IOType.D2.min(axis: Int) = reduce(axis) { acc, i -> minOf(acc, i) }

fun IOType.D3.min() = value.toFloatArray().min()

fun IOType.D3.min(axis: Int) = reduce(axis) { acc, i -> minOf(acc, i) }

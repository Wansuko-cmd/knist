package com.wsr.core.collection.sum

import com.wsr.core.IOType
import com.wsr.core.collection.reduce.reduce

fun IOType.D1.sum() = value.toFloatArray().sum()

fun IOType.D2.sum() = value.toFloatArray().sum()

fun IOType.D2.sum(axis: Int): IOType.D1 = reduce(axis) { acc, i -> acc + i }

fun IOType.D3.sum() = value.toFloatArray().sum()

fun IOType.D3.sum(axis: Int) = reduce(axis) { acc, i -> acc + i }

fun IOType.D4.sum() = value.toFloatArray().sum()

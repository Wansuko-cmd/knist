package com.wsr.core.collection.max

import com.wsr.core.IOType
import com.wsr.core.collection.reduce.reduce

fun IOType.D1.max() = value.toFloatArray().max()

fun IOType.D2.max() = value.toFloatArray().max()

fun IOType.D2.max(axis: Int) = reduce(axis) { acc, i -> maxOf(acc, i) }

fun IOType.D3.max() = value.toFloatArray().max()

fun IOType.D3.max(axis: Int) = reduce(axis) { acc, i -> maxOf(acc, i) }

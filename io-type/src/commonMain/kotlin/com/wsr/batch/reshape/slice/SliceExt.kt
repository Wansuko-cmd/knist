package com.wsr.batch.reshape.slice

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.reshape.slice.slice

fun Batch<IOType.D1>.slice(i: IntRange) = map { it.slice(i = i) }

fun Batch<IOType.D2>.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1]) =
    map { it.slice(i = i, j = j) }

fun Batch<IOType.D3>.slice(
    i: IntRange = 0 until shape[0],
    j: IntRange = 0 until shape[1],
    k: IntRange = 0 until shape[2],
) = map { it.slice(i = i, j = j, k = k) }

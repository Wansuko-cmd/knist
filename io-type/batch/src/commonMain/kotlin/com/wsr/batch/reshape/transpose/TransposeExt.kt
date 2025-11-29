package com.wsr.batch.reshape.transpose

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.reshape.transpose.transpose

fun Batch<IOType.D2>.transpose() = map { it.transpose() }

fun Batch<IOType.D3>.transpose(axisI: Int, axisJ: Int, axisK: Int) =
    map { it.transpose(axisI = axisI, axisJ = axisJ, axisK = axisK) }

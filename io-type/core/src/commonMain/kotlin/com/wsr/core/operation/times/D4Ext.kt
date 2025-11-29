package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.get

operator fun IOType.D4.times(other: Float): IOType.D4 = IOType.d4(shape) { i, j, k, l -> this[i, j, k, l] * other }

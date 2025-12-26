package com.wsr.core.operation.plus

import com.wsr.core.IOType
import com.wsr.core.d4
import com.wsr.core.get

operator fun IOType.D4.plus(other: IOType.D4): IOType.D4 = IOType.d4(shape) { i, j, k, l ->
    this[i, j, k, l] + other[i, j, k, l]
}

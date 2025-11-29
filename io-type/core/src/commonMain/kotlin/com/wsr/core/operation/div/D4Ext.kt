package com.wsr.core.operation.div

import com.wsr.core.IOType
import com.wsr.core.d4
import com.wsr.core.get

operator fun IOType.D4.div(other: Float): IOType.D4 = IOType.d4(this.shape) { i, j, k, l ->
    this[i, j, k, l] / other
}


operator fun IOType.D4.div(other: IOType.D4): IOType.D4 = IOType.d4(this.shape) { i, j, k, l ->
    this[i, j, k, l] / other[i, j, k, l]
}

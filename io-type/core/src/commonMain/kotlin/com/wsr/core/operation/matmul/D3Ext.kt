package com.wsr.core.operation.matmul

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.set

infix fun IOType.D3.matMul(other: IOType.D3): IOType.D3 {
    val result = IOType.d3(listOf(shape[0], shape[1], other.shape[2]))
    for (i in 0 until shape[0]) result[i] = this[i] matMul other[i]
    return result
}

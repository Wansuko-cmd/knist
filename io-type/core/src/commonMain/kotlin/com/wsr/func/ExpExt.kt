package com.wsr.func

import com.wsr.IOType

fun IOType.D2.exp() = IOType.D2(
    shape = shape,
    value = FloatArray(value.size) { kotlin.math.exp(value[it]) },
)

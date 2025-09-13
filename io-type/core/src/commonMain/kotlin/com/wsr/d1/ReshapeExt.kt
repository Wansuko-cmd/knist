package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.toD2() = IOType.d2(
    shape = listOf(size, first().shape[0]),
    value = map { it.value }.reduce { acc, it -> acc + it },
)

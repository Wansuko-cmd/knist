package com.wsr.reshape

import com.wsr.IOType

fun List<IOType.D1>.toD2(): IOType.D2 = IOType.d2(size, first().shape[0]) { i, j -> this[i][j] }

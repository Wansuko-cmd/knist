package com.wsr.core.reshape.slice

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun IOType.D1.slice(i: IntRange) = IOType.d1(i.count()) { this[i.start + it] }

fun IOType.D2.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1]) =
    IOType.d2(shape = listOf(i.count(), j.count())) { x, y ->
        this[i.start + x, j.start + y]
    }

fun IOType.D3.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1], k: IntRange = 0 until shape[2]) =
    IOType.d3(shape = listOf(i.count(), j.count(), k.count())) { x, y, z ->
        this[i.start + x, j.start + y, k.start + z]
    }

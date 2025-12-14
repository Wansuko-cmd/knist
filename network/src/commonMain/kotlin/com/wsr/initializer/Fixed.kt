package com.wsr.initializer

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4

class Fixed(private val value: Float) : WeightInitializer {
    override fun d1(input: List<Int>, output: List<Int>, size: Int): IOType.D1 = IOType.d1(size) { value }

    override fun d2(input: List<Int>, output: List<Int>, x: Int, y: Int): IOType.D2 =
        IOType.d2(shape = listOf(x, y)) { _, _ -> value }

    override fun d3(input: List<Int>, output: List<Int>, x: Int, y: Int, z: Int): IOType.D3 =
        IOType.d3(shape = listOf(x, y, z)) { _, _, _ -> value }

    override fun d4(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int, l: Int): IOType.D4 =
        IOType.d4(shape = listOf(i, j, k, l)) { _, _, _, _ -> value }
}

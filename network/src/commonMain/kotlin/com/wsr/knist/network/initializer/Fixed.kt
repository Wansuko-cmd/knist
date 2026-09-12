package com.wsr.knist.network.initializer

import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.d4
import kotlinx.serialization.Serializable

@Serializable
class Fixed(private val value: Float) : WeightInitializer {
    override fun d1(input: List<Int>, output: List<Int>, size: Int): IOType.D1.Global = IOType.d1(size) { value }

    override fun d2(input: List<Int>, output: List<Int>, i: Int, j: Int): IOType.D2.Global = IOType.d2(shape = listOf(i, j)) { _, _ -> value }

    override fun d3(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int): IOType.D3.Global = IOType.d3(shape = listOf(i, j, k)) { _, _, _ -> value }

    override fun d4(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int, l: Int): IOType.D4.Global = IOType.d4(shape = listOf(i, j, k, l)) { _, _, _, _ -> value }
}

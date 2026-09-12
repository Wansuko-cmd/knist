package com.wsr.knist.network.initializer

import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.d4
import com.wsr.knist.network.nextFloat
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Random(private val seed: Int? = null, private val from: Float = -1f, private val until: Float = 1f) : WeightInitializer {
    @Transient
    private val random = seed?.let { Random(it) } ?: Random
    override fun d1(input: List<Int>, output: List<Int>, size: Int): IOType.D1.Global = IOType.d1(size) { random.nextFloat(from, until) }

    override fun d2(input: List<Int>, output: List<Int>, i: Int, j: Int): IOType.D2.Global = IOType.d2(shape = listOf(i, j)) { _, _ -> random.nextFloat(from, until) }

    override fun d3(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int): IOType.D3.Global = IOType.d3(shape = listOf(i, j, k)) { _, _, _ -> random.nextFloat(from, until) }

    override fun d4(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int, l: Int): IOType.D4.Global = IOType.d4(shape = listOf(i, j, k, l)) { _, _, _, _ -> random.nextFloat(from, until) }
}

package com.wsr.initializer

import com.wsr.IOType
import kotlinx.serialization.Serializable

@Serializable
class Fixed(private val value: Double) : WeightInitializer {
    override fun d1(
        input: List<Int>,
        output: List<Int>,
        size: Int,
    ): IOType.D1 = IOType.d1(size) { value }

    override fun d2(
        input: List<Int>,
        output: List<Int>,
        x: Int,
        y: Int,
    ): IOType.D2 = IOType.d2(shape = listOf(x, y)) { _, _ -> value }

    override fun d3(
        input: List<Int>,
        output: List<Int>,
        x: Int,
        y: Int,
        z: Int,
    ): IOType.D3 = IOType.d3(shape = listOf(x, y, z)) { _, _, _ -> value }

    override fun d4(
        input: List<Int>,
        output: List<Int>,
        i: Int,
        j: Int,
        k: Int,
        n: Int,
    ): IOType.D4 = IOType.d4(shape = listOf(i, j, k, n)) { _, _, _, _ -> value }
}

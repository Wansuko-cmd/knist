package com.wsr.initializer

import com.wsr.IOType
import kotlinx.serialization.Serializable
import kotlin.math.sqrt
import kotlin.random.Random

@Serializable
class He(private val seed: Int? = null) : WeightInitializer {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    override fun d1(
        input: List<Int>,
        output: List<Int>,
        size: Int,
    ): IOType.D1 {
        val weight = calcWeight(
            fanIn = input.reduce { acc, i -> acc * i },
            size = size,
        )
        return IOType.d1(value = weight)
    }

    override fun d2(
        input: List<Int>,
        output: List<Int>,
        x: Int,
        y: Int,
    ): IOType.D2 {
        val weight = calcWeight(
            fanIn = input.reduce { acc, i -> acc * i },
            size = x * y,
        )
        return IOType.d2(shape = listOf(x, y), value = weight)
    }

    override fun d3(
        input: List<Int>,
        output: List<Int>,
        x: Int,
        y: Int,
        z: Int,
    ): IOType.D3 {
        val weight = calcWeight(
            fanIn = input.reduce { acc, i -> acc * i },
            size = x * y * z,
        )
        return IOType.d3(shape = listOf(x, y, z), value = weight)
    }

    override fun d4(
        input: List<Int>,
        output: List<Int>,
        i: Int,
        j: Int,
        k: Int,
        n: Int,
    ): IOType.D4 {
        val weight = calcWeight(
            fanIn = input.reduce { acc, i -> acc * i },
            size = i * j * k * n,
        )
        return IOType.d4(shape = listOf(i, j, k, n), value = weight)
    }

    private fun calcWeight(fanIn: Int, size: Int): DoubleArray {
        val limit = sqrt(6.0 / fanIn)
        return DoubleArray(size) { random.nextDouble(-limit, limit) }
    }
}

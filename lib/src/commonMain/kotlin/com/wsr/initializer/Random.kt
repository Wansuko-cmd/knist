package com.wsr.initializer

import com.wsr.IOType
import kotlin.random.Random

class Random(seed: Int? = null, private val from: Double = -1.0, private val until: Double = 1.0) : WeightInitializer {
    private val random = seed?.let { Random(it) } ?: Random
    override fun d1(input: List<Int>, output: List<Int>, size: Int): IOType.D1 =
        IOType.d1(size) { random.nextDouble(from, until) }

    override fun d2(input: List<Int>, output: List<Int>, x: Int, y: Int): IOType.D2 =
        IOType.d2(shape = listOf(x, y)) { _, _ -> random.nextDouble(from, until) }

    override fun d3(input: List<Int>, output: List<Int>, x: Int, y: Int, z: Int): IOType.D3 =
        IOType.d3(shape = listOf(x, y, z)) { _, _, _ -> random.nextDouble(from, until) }

    override fun d4(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int, n: Int): IOType.D4 =
        IOType.d4(shape = listOf(i, j, k, n)) { _, _, _, _ -> random.nextDouble(from, until) }
}

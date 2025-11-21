@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")

package com.wsr.optimizer

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

import com.wsr.get

class OptimizerTest {
    // D1のテスト
    @Test
    fun `D1で勾配のnormがmaxNormを超える場合_clipが適用される`() {
        val optimizer = object : Optimizer.D1(_maxNorm = 1.0f) {
            override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
                // 単純にdwをそのまま返す（clipの効果のみを確認）
                return dw
            }
        }

        val weight = IOType.d1(listOf(10.0f, 20.0f, 30.0f))
        val dw = IOType.d1(listOf(3.0f, 4.0f, 0.0f)) // norm = 5.0f

        val result = optimizer.adapt(weight, dw)

        // scaled dw = [3.0f, 4.0f, 0.0f] * (1.0f/5.0f) = [0.6f, 0.8f, 0.0f]
        assertEquals(expected = 0.6f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.8f, actual = result[1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[2], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `D1で勾配のnormがmaxNorm以下の場合_clipは適用されない`() {
        val optimizer = object : Optimizer.D1(_maxNorm = 10.0f) {
            override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 = dw
        }

        val weight = IOType.d1(listOf(10.0f, 20.0f, 30.0f))
        val dw = IOType.d1(listOf(1.0f, 2.0f, 3.0f)) // norm ≈ 3.74f

        val result = optimizer.adapt(weight, dw)

        // clip不要なので元の値のまま
        assertEquals(expected = 1.0f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 2.0f, actual = result[1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 3.0f, actual = result[2], absoluteTolerance = 1e-6f)
    }

    // D2のテスト
    @Test
    fun `D2で勾配のnormがmaxNormを超える場合_clipが適用される`() {
        val optimizer = object : Optimizer.D2(_maxNorm = 2.0f) {
            override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 = dw
        }

        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        // dw = [[6, 0], [0, 8]] -> norm = 10.0f
        val dw = IOType.d2(2, 2) { x, y ->
            when {
                x == 0 && y == 0 -> 6.0f
                x == 1 && y == 1 -> 8.0f
                else -> 0.0f
            }
        }

        val result = optimizer.adapt(weight, dw)

        // scaled dw = [[6, 0], [0, 8]] * (2.0f/10.0f) = [[1.2f, 0], [0, 1.6f]]
        assertEquals(expected = 1.2f, actual = result[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.6f, actual = result[1, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `D2で勾配のnormがmaxNorm以下の場合_clipは適用されない`() {
        val optimizer = object : Optimizer.D2(_maxNorm = 20.0f) {
            override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 = dw
        }

        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        val result = optimizer.adapt(weight, dw)

        // clip不要なので元の値のまま
        assertEquals(expected = 1.0f, actual = result[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 2.0f, actual = result[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 3.0f, actual = result[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 4.0f, actual = result[1, 1], absoluteTolerance = 1e-6f)
    }

    // D3のテスト
    @Test
    fun `D3で勾配のnormがmaxNormを超える場合_clipが適用される`() {
        val optimizer = object : Optimizer.D3(_maxNorm = 1.0f) {
            override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 = dw
        }

        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        // dw = [[[12, 0], [0, 0]], [[0, 0], [0, 0]]] -> norm = 12.0f
        val dw = IOType.d3(2, 2, 2) { x, y, z ->
            if (x == 0 && y == 0 && z == 0) 12.0f else 0.0f
        }

        val result = optimizer.adapt(weight, dw)

        // scaled dw = [[[12, 0], [0, 0]], [[0, 0], [0, 0]]] * (1.0f/12.0f) = [[[1, 0], [0, 0]], [[0, 0], [0, 0]]]
        assertEquals(expected = 1.0f, actual = result[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[0, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[0, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[0, 1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[1, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[1, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.0f, actual = result[1, 1, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `D3で勾配のnormがmaxNorm以下の場合_clipは適用されない`() {
        val optimizer = object : Optimizer.D3(_maxNorm = 100.0f) {
            override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 = dw
        }

        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val dw = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }

        val result = optimizer.adapt(weight, dw)

        // clip不要なので元の値のまま
        assertEquals(expected = 1.0f, actual = result[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 2.0f, actual = result[0, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 3.0f, actual = result[0, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 4.0f, actual = result[0, 1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 5.0f, actual = result[1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 6.0f, actual = result[1, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.0f, actual = result[1, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 8.0f, actual = result[1, 1, 1], absoluteTolerance = 1e-6f)
    }
}

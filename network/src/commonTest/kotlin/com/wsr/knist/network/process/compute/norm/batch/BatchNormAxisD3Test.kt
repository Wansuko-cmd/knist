@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.batch

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.get
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.batch.d3.BatchNormAxisD3
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class BatchNormAxisD3Test {
    // 全ての次元の長さを変えて、axisの取り違えを検出する
    private val n = 2
    private val di = 2
    private val dj = 3
    private val dk = 4
    private val e = 1e-6f
    private val momentum = 0.9f

    private fun target(axis: Int) = BatchNormAxisD3(inputI = di, inputJ = dj, inputK = dk, axis = axis, e = e, momentum = momentum)

    private fun x(n: Int, i: Int, j: Int, k: Int) = ((n * 7 + i * 5 + j * 3 + k * 11 + i * j * k) % 13) / 3f

    private val input
        get() = Batch.of(
            *Array(n) { b -> IOType.d3(di, dj, dk) { i, j, k -> x(b, i, j, k) } },
        )

    /** axisとbatchを潰した単純なループによる参照実装 */
    private class Reference(val axis: Int, val n: Int, val di: Int, val dj: Int, val dk: Int, val e: Float, val momentum: Float, val x: (Int, Int, Int, Int) -> Float) {
        private fun key(i: Int, j: Int, k: Int) = when (axis) {
            0 -> j * dk + k
            1 -> i * dk + k
            else -> i * dj + j
        }

        private val members = HashMap<Int, MutableList<IntArray>>().also { map ->
            for (b in 0 until n) for (i in 0 until di) for (j in 0 until dj) for (k in 0 until dk) {
                map.getOrPut(key(i, j, k)) { mutableListOf() }.add(intArrayOf(b, i, j, k))
            }
        }
        private val mean = members.mapValues { (_, v) -> v.map { x(it[0], it[1], it[2], it[3]) }.average().toFloat() }
        private val variance = members.mapValues { (key, v) -> v.map { (x(it[0], it[1], it[2], it[3]) - mean[key]!!).let { d -> d * d } }.average().toFloat() }

        fun output(b: Int, i: Int, j: Int, k: Int): Float {
            val key = key(i, j, k)
            return (x(b, i, j, k) - mean[key]!!) / sqrt(variance[key]!! + e)
        }

        /** delta = output^2 を伝播した勾配 */
        fun dx(b: Int, i: Int, j: Int, k: Int): Float {
            val key = key(i, j, k)
            val group = members[key]!!
            val deltas = group.map { output(it[0], it[1], it[2], it[3]).let { o -> o * o } }
            val outputs = group.map { output(it[0], it[1], it[2], it[3]) }
            val meanDelta = deltas.average().toFloat()
            val meanDeltaOutput = deltas.zip(outputs).map { (d, o) -> d * o }.average().toFloat()
            val delta = output(b, i, j, k).let { it * it }
            return (delta - meanDelta - output(b, i, j, k) * meanDeltaOutput) / sqrt(variance[key]!! + e)
        }

        /** 1回trainした後のexpect */
        fun expect(b: Int, i: Int, j: Int, k: Int): Float {
            val key = key(i, j, k)
            val rm = (1 - momentum) * mean[key]!!
            val rv = momentum * 1f + (1 - momentum) * variance[key]!!
            return (x(b, i, j, k) - rm) / sqrt(rv + e)
        }
    }

    private fun reference(axis: Int) = Reference(axis, n, di, dj, dk, e, momentum, ::x)

    private fun assertEach(actual: Batch<IOType.D3>, expected: (Int, Int, Int, Int) -> Float) {
        for (b in 0 until n) for (i in 0 until di) for (j in 0 until dj) for (k in 0 until dk) {
            assertEquals(expected(b, i, j, k), actual[b][i, j, k].unwrap(), absoluteTolerance = 1e-3f, message = "[$b, $i, $j, $k]")
        }
    }

    private fun testTrain(axis: Int) = networkScopeTestRule {
        val reference = reference(axis)
        val actual = with(target(axis)) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = {
                    val output = it as Batch<IOType.D3>
                    output * output
                },
            )
        } as Batch<IOType.D3>

        assertEach(actual, reference::dx)
    }

    private fun testExpect(axis: Int) = networkScopeTestRule {
        val reference = reference(axis)
        val target = target(axis)
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertEach(actual, reference::expect)
    }

    @Test
    fun `Axis0_train=batchとaxis0で正規化および勾配を伝播`() = testTrain(axis = 0)

    @Test
    fun `Axis1_train=batchとaxis1で正規化および勾配を伝播`() = testTrain(axis = 1)

    @Test
    fun `Axis2_train=batchとaxis2で正規化および勾配を伝播`() = testTrain(axis = 2)

    @Test
    fun `Axis0_expect=trainで更新した統計量で正規化`() = testExpect(axis = 0)

    @Test
    fun `Axis1_expect=trainで更新した統計量で正規化`() = testExpect(axis = 1)

    @Test
    fun `Axis2_expect=trainで更新した統計量で正規化`() = testExpect(axis = 2)
}

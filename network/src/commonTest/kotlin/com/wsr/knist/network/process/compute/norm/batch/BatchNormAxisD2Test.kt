@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.batch

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.batch.d2.BatchNormAxisD2
import kotlin.test.Test

class BatchNormAxisD2Test {
    // inputI != inputJ としてaxisの取り違えを検出する
    val target0 get() = BatchNormAxisD2(inputI = 2, inputJ = 3, axis = 0, e = 1e-6f, momentum = 0.9f)
    val target1 get() = BatchNormAxisD2(inputI = 2, inputJ = 3, axis = 1, e = 1e-6f, momentum = 0.9f)
    val input
        get() = Batch.of(
            IOType.d2(
                IOType.d1(3) { it.toFloat() },
                IOType.d1(3) { it * 2f },
            ),
            IOType.d2(
                IOType.d1(3) { 10f % (it + 1) },
                IOType.d1(3) { it * 0.3f },
            ),
        )

    @Test
    fun `Axis0_train=batchとaxis0で正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target0) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = {
                    val output = it as Batch<IOType.D2>
                    output * output
                },
            )
        } as Batch<IOType.D2>

        assertContentEquals(IOType.d1(0.0000f, -1.3821f, -0.7961f), actual[0][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, 0.7279f, 0.3328f), actual[0][1], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, 0.9000f, -0.0422f), actual[1][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, -0.2458f, 0.5055f), actual[1][1], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `Axis0_expect=trainで更新した統計量で正規化`() = networkScopeTestRule {
        val target = target0
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(IOType.d1(0.0000f, 0.9368f, 1.7473f), actual[0][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, 1.9579f, 3.6781f), actual[0][1], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, -0.0842f, 0.7820f), actual[1][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.0000f, 0.2221f, 0.3958f), actual[1][1], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `Axis1_train=batchとaxis1で正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target1) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = {
                    val output = it as Batch<IOType.D2>
                    output * output
                },
            )
        } as Batch<IOType.D2>

        assertContentEquals(IOType.d1(0.4830f, -1.4490f, 1.4490f), actual[0][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.3549f, -0.9022f, 0.4965f), actual[0][1], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.4830f, 0.4830f, -1.4490f), actual[1][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(0.3549f, -0.0030f, -0.3011f), actual[1][1], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `Axis1_expect=trainで更新した統計量で正規化`() = networkScopeTestRule {
        val target = target1
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(IOType.d1(-0.0682f, 0.9548f, 1.9778f), actual[0][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(-0.1092f, 1.7903f, 3.6898f), actual[0][1], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(-0.0682f, -0.0682f, 0.9548f), actual[1][0], absoluteTolerance = 1e-3f)
        assertContentEquals(IOType.d1(-0.1092f, 0.1757f, 0.4606f), actual[1][1], absoluteTolerance = 1e-3f)
    }
}

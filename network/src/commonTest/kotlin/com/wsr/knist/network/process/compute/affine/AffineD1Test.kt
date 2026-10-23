@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.affine

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.sgd.Sgd
import kotlin.test.Test

class AffineD1Test {
    val target
        get() = AffineD1(
            outputI = 4,
            inputI = 3,
            optimizer = Sgd(scheduler = Scheduler.Fix(rate = 0.01f)).d2(3, 4),
            weight = IOType.d2(3, 4) { i, j -> i * 2f + j },
        )
    val input
        get() = Batch.of(
            IOType.d1(3) { it * 2f },
            IOType.d1(3) { it * 3f },
        )

    @Test
    fun `expect=全結合`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(20f, 26f, 32f, 38f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(30f, 39f, 48f, 57f), actual = actual[1])
    }

    @Test
    fun `train=逆伝播を行い勾配を返す`() = networkScopeTestRule {
        val actual = with(target) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(204f, 436f, 668f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(306f, 654f, 1002f), actual = actual[1])
    }

    @Test
    fun `train=重みを更新する`() = networkScopeTestRule {
        val target = target

        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(13.5000f, 17.5500f, 21.6000f, 25.6500f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(20.2500f, 26.3250f, 32.4000f, 38.4750f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
    }
}

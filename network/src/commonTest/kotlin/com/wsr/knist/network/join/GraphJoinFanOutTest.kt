@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network.join

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.Network
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.join.add.add
import com.wsr.knist.network.networkTestRule
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.sgd.Sgd
import com.wsr.knist.network.output.mean.meanSquare
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.debug.debug
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

/**
 * design docのresidualパターン（h2 = add(h, f), f = h.someLayer()）をBuilder DSLで組む最小構成。
 * h は「joinへ直接」「f経由」の2箇所から消費されるfan-outノードになる。
 */
class GraphJoinFanOutTest {

    private val x = 2f
    private val w0 = 3f
    private val w1 = 2f
    private val label = 5f

    private var capturedDelta: Batch<IOType.D1>? = null

    private fun createNetwork(rate: Float = 0.1f): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = Network.create(
        port = port(RawD1(1)),
        optimizer = Sgd(scheduler = Scheduler.Fix(rate = rate)),
        initializer = Fixed(0f),
    ) { input ->
        val h = input
            .affine(neuron = 1, id = "shared", initializer = Fixed(w0))
            .debug(onDelta = { capturedDelta = it })
        val f = h.affine(neuron = 1, id = "branch", initializer = Fixed(w1))
        add(h, f).meanSquare()
    }

    private val input = Batch(1) { IOType.d1(x) }
    private val labelBatch = Batch(1) { IOType.d1(label) }

    @Test
    fun `add=hを直接とf経由の両方で消費しても正しい値に合流する`() = networkTestRule {
        val network = createNetwork()
        runTest {
            val output = network.expect(input)

            val h = w0 * x
            val f = w1 * h
            assertContentEquals(expected = Batch.of(IOType.d1(h + f)), actual = output)
        }
    }

    /**
     * fan-outノード(h)のdeltaは、joinへの直接寄与とf経由の寄与の合算になるべき
     * （01-design.md の acc.add・02-work-plan.md Phase2チェックリストの検証観点）
     */
    @Test
    fun `add=fan-outしたノードのdeltaはjoin直結分とf経由分の和になる`() = networkTestRule {
        val network = createNetwork()
        runTest {
            network.train(input = input, label = labelBatch)

            val h = w0 * x
            val f = w1 * h
            val joined = h + f
            val deltaJoin = joined - label
            val expectedDeltaForH = deltaJoin + w1 * deltaJoin

            assertContentEquals(
                expected = Batch.of(IOType.d1(expectedDeltaForH)),
                actual = capturedDelta!!,
            )
        }
    }

    /**
     * 1ステップ目の残骸（fan-out用の累積状態やSourceの未読delta）が
     * 2ステップ目のdeltaへ漏れないことの確認
     */
    @Test
    fun `add=連続して学習しても前ステップの累積が次ステップへ漏れない`() = networkTestRule {
        // 学習率0で重みを固定し、純粋に「前ステップの蓄積が漏れていないか」だけを見る
        val network = createNetwork(rate = 0f)
        runTest {
            network.train(input = input, label = labelBatch)
            val firstDelta = capturedDelta!!

            network.train(input = input, label = labelBatch)
            val secondDelta = capturedDelta!!

            assertContentEquals(expected = firstDelta, actual = secondDelta)
        }
    }
}

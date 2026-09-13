@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.converter.raw.RawD2
import com.wsr.knist.network.converter.raw.RawD3
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.freeze.Freeze
import com.wsr.knist.network.optimizer.sgd.Sgd
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.mean.meanSquare
import com.wsr.knist.network.process.compute.affine.AffineD1
import com.wsr.knist.network.process.compute.affine.AffineD2
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.bias.d1.BiasD1
import com.wsr.knist.network.process.compute.bias.d1.bias
import com.wsr.knist.network.process.compute.scale.d3.ScaleD3
import com.wsr.knist.network.process.compute.scale.d3.scale
import com.wsr.knist.network.process.reshape.reshape.ReshapeD1ToD2
import com.wsr.knist.network.process.reshape.reshape.ReshapeD1ToD3
import com.wsr.knist.network.process.reshape.reshape.ReshapeD2ToD1
import com.wsr.knist.network.process.reshape.reshape.ReshapeD2ToD3
import com.wsr.knist.network.process.reshape.reshape.ReshapeD3ToD1
import com.wsr.knist.network.process.reshape.reshape.ReshapeD3ToD2
import com.wsr.knist.network.process.reshape.reshape.reshapeToD1
import com.wsr.knist.network.process.reshape.reshape.reshapeToD2
import com.wsr.knist.network.process.reshape.reshape.reshapeToD3
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class NetworkReplaceTest {

    private val optimizer = Sgd(scheduler = Scheduler.Fix(rate = 0.01f))

    private fun createD1Network(): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = Network.create(
        port = port(RawD1(3)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.affine(neuron = 4, id = "mid").affine(neuron = 2, id = "last").meanSquare()
    }

    private fun createD2Network(): Network.Src1.Sink1<Batch<IOType.D2>, Batch<IOType.D2>> = Network.create(
        port = port(RawD2(2, 3)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.affine(neuron = 4, id = "mid").meanSquare()
    }

    private fun createD3Network(): Network.Src1.Sink1<Batch<IOType.D3>, Batch<IOType.D1>> = Network.create(
        port = port(RawD3(2, 2, 2)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.scale(id = "mid").reshapeToD1().meanSquare()
    }

    private fun createD3OutputNetwork(): Network.Src1.Sink1<Batch<IOType.D3>, Batch<IOType.D3>> = Network.create(
        port = port(RawD3(2, 2, 2)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.scale(id = "mid").meanSquare()
    }

    private val inputD1 = Batch(1) { IOType.d1(1f, 2f, 3f) }
    private val inputD2 = Batch(1) { IOType.d2(2, 3) { i, j -> (i * 3 + j + 1).toFloat() } }
    private val inputD3 = Batch(1) { IOType.d3(2, 2, 2) { i, j, k -> (i * 4 + j * 2 + k + 1).toFloat() } }
    private val labelD1 = Batch(1) { IOType.d1(0f, 1f) }

    /**
     * Optimizer の差し替え・凍結
     */
    @Test
    fun `replace_Optimizer=Freezeで凍結でき通常のOptimizerで解凍できる`() = networkTestRule {
        val frozen = createD1Network().replace(condition = { true }, optimizer = Freeze)

        runTest {
            val before = frozen.expect(inputD1)
            frozen.train(input = inputD1, label = labelD1)
            assertSameOutput(expected = before, actual = frozen.expect(inputD1))

            val unfrozen = frozen.replace(condition = { true }, optimizer = optimizer)
            unfrozen.train(input = inputD1, label = labelD1)
            assertDifferentOutput(before = before, after = unfrozen.expect(inputD1))
        }
    }

    @Test
    fun `replace_Optimizer=凍結した層より前段にdeltaが流れて学習が継続する`() = networkTestRule {
        val frozen = createD1Network().replace(condition = { it.id == "last" }, optimizer = Freeze)

        runTest {
            val before = frozen.expect(inputD1)
            frozen.train(input = inputD1, label = labelD1)
            assertDifferentOutput(before = before, after = frozen.expect(inputD1))
        }
    }

    /**
     * Compute の差し替え
     */
    @Test
    fun `replace_ComputeD1=層をDSLで差し替えられる`() = networkTestRule {
        val replaced = createD1Network().replace<AffineD1>(
            initializer = Fixed(1f),
            condition = { it.id == "mid" },
        ) { affine(neuron = 4) }

        val expected = Network.create(
            port = port(RawD1(3)),
            optimizer = optimizer,
            initializer = Fixed(0.5f),
        ) { builder ->
            builder.affine(neuron = 4, initializer = Fixed(1f)).affine(neuron = 2).meanSquare()
        }

        runTest {
            assertSameOutput(expected = expected.expect(inputD1), actual = replaced.expect(inputD1))
            assertDifferentOutput(before = createD1Network().expect(inputD1), after = replaced.expect(inputD1))
        }
    }

    @Test
    fun `replace_ComputeD2=層をDSLで差し替えられる`() = networkTestRule {
        val replaced = createD2Network().replace<AffineD2>(
            initializer = Fixed(1f),
            condition = { it.id == "mid" },
        ) { affine(neuron = 4) }

        val expected = Network.create(
            port = port(RawD2(2, 3)),
            optimizer = optimizer,
            initializer = Fixed(0.5f),
        ) { builder ->
            builder.affine(neuron = 4, initializer = Fixed(1f)).meanSquare()
        }

        runTest {
            assertSameOutput(expected = expected.expect(inputD2), actual = replaced.expect(inputD2))
            assertDifferentOutput(before = createD2Network().expect(inputD2), after = replaced.expect(inputD2))
        }
    }

    @Test
    fun `replace_ComputeD3=層をDSLで差し替えられる`() = networkTestRule {
        val replaced = createD3Network().replace<ScaleD3>(
            condition = { it.id == "mid" },
        ) { scale(initializer = Fixed(2f)) }

        val expected = Network.create(
            port = port(RawD3(2, 2, 2)),
            optimizer = optimizer,
            initializer = Fixed(0.5f),
        ) { builder ->
            builder.scale(initializer = Fixed(2f)).reshapeToD1().meanSquare()
        }

        runTest {
            assertSameOutput(expected = expected.expect(inputD3), actual = replaced.expect(inputD3))
            assertDifferentOutput(before = createD3Network().expect(inputD3), after = replaced.expect(inputD3))
        }
    }

    /**
     * Reshape の差し替え
     * 値を変えないネットワークを使い、同じ形状変換で置き換えても出力が保たれることを確認する
     */
    private fun createReshapeNetworkA(): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = Network.create(
        port = port(RawD1(6)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder
            .reshapeToD2(i = 2, j = 3, id = "d1tod2")
            .reshapeToD3(i = 1, j = 2, k = 3, id = "d2tod3")
            .reshapeToD1(id = "d3tod1")
            .meanSquare()
    }

    private fun createReshapeNetworkB(): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = Network.create(
        port = port(RawD1(8)),
        optimizer = optimizer,
        initializer = Fixed(0.5f),
    ) { builder ->
        builder
            .reshapeToD3(i = 2, j = 2, k = 2, id = "d1tod3")
            .reshapeToD2(i = 2, j = 4, id = "d3tod2")
            .reshapeToD1(id = "d2tod1")
            .meanSquare()
    }

    private val inputD1x6 = Batch(1) { IOType.d1(6) { it + 1f } }
    private val inputD1x8 = Batch(1) { IOType.d1(8) { it + 1f } }

    @Test
    fun `replace_ReshapeD1ToD2=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkA()
        val replaced = original.replace<ReshapeD1ToD2>(
            condition = { it.id == "d1tod2" },
        ) { reshapeToD2(i = 2, j = 3) }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x6), actual = replaced.expect(inputD1x6))
        }
    }

    @Test
    fun `replace_ReshapeD2ToD3=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkA()
        val replaced = original.replace<ReshapeD2ToD3>(
            condition = { it.id == "d2tod3" },
        ) { reshapeToD3(i = 1, j = 2, k = 3) }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x6), actual = replaced.expect(inputD1x6))
        }
    }

    @Test
    fun `replace_ReshapeD3ToD1=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkA()
        val replaced = original.replace<ReshapeD3ToD1>(
            condition = { it.id == "d3tod1" },
        ) { reshapeToD1() }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x6), actual = replaced.expect(inputD1x6))
        }
    }

    @Test
    fun `replace_ReshapeD1ToD3=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkB()
        val replaced = original.replace<ReshapeD1ToD3>(
            condition = { it.id == "d1tod3" },
        ) { reshapeToD3(i = 2, j = 2, k = 2) }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x8), actual = replaced.expect(inputD1x8))
        }
    }

    @Test
    fun `replace_ReshapeD3ToD2=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkB()
        val replaced = original.replace<ReshapeD3ToD2>(
            condition = { it.id == "d3tod2" },
        ) { reshapeToD2(i = 2, j = 4) }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x8), actual = replaced.expect(inputD1x8))
        }
    }

    @Test
    fun `replace_ReshapeD2ToD1=層をDSLで差し替えられる`() = networkTestRule {
        val original = createReshapeNetworkB()
        val replaced = original.replace<ReshapeD2ToD1>(
            condition = { it.id == "d2tod1" },
        ) { reshapeToD1() }

        runTest {
            assertSameOutput(expected = original.expect(inputD1x8), actual = replaced.expect(inputD1x8))
        }
    }

    /**
     * Converter の差し替え
     */
    @Test
    fun `replaceSource=入力Converterを差し替えられる`() = networkTestRule {
        val original = createD1Network()
        val replaced = original.replaceSource(RawD1(3))

        runTest {
            assertSameOutput(expected = original.expect(inputD1), actual = replaced.expect(inputD1))
        }
    }

    /**
     * Output（損失関数）の差し替え
     */
    @Test
    fun `replaceSinkD1=出力層を組み直せる`() = networkTestRule {
        val original = createD1Network()
        val replaced = original.replaceSink<Output.D1, Batch<IOType.D1>> { meanSquare() }

        runTest {
            assertSameOutput(expected = original.expect(inputD1), actual = replaced.expect(inputD1))
        }
    }

    @Test
    fun `replaceSinkD2=出力層を組み直せる`() = networkTestRule {
        val original = createD2Network()
        val replaced = original.replaceSink<Output.D2, Batch<IOType.D2>> { meanSquare() }

        runTest {
            assertSameOutput(expected = original.expect(inputD2), actual = replaced.expect(inputD2))
        }
    }

    @Test
    fun `replaceSinkD3=出力層を組み直せる`() = networkTestRule {
        val original = createD3OutputNetwork()
        val replaced = original.replaceSink<Output.D3, Batch<IOType.D3>> { meanSquare() }

        runTest {
            assertSameOutput(expected = original.expect(inputD3), actual = replaced.expect(inputD3))
        }
    }

    @Test
    fun `replaceSink=最終層の形状と合わない型パラメータは例外`() = networkTestRule {
        assertFailsWith<IllegalStateException> {
            createD1Network().replaceSink<Output.D2, Batch<IOType.D2>> { meanSquare() }
        }
    }

    /**
     * 空ブロック・形状チェック
     */
    @Test
    fun `replace=空ブロックで入出力形状が同じ層は削除できる`() = networkTestRule {
        val original = Network.create(
            port = port(RawD1(3)),
            optimizer = optimizer,
            initializer = Fixed(0.5f),
        ) { builder ->
            builder.bias(initializer = Fixed(1f), id = "del").meanSquare()
        }
        val replaced = original.replace<BiasD1>(condition = { it.id == "del" }) { this }

        val expected = Network.create(
            port = port(RawD1(3)),
            optimizer = optimizer,
            initializer = Fixed(0.5f),
        ) { builder ->
            builder.meanSquare()
        }

        runTest {
            assertSameOutput(expected = expected.expect(inputD1), actual = replaced.expect(inputD1))
            assertDifferentOutput(before = original.expect(inputD1), after = replaced.expect(inputD1))
        }
    }

    @Test
    fun `replace=空ブロックで入出力形状が変わる層は例外`() = networkTestRule {
        assertFailsWith<IllegalStateException> {
            createD1Network().replace<AffineD1>(condition = { it.id == "mid" }) { this }
        }
    }

    @Test
    fun `replace=差し替え後の形状が一致しない場合は例外`() = networkTestRule {
        assertFailsWith<IllegalStateException> {
            createD1Network().replace<AffineD1>(condition = { it.id == "mid" }) { affine(neuron = 5) }
        }
    }

    private fun <T : IOType> assertSameOutput(expected: Batch<T>, actual: Batch<T>) {
        val e = expected.value.toFloatArray()
        val a = actual.value.toFloatArray()
        assertTrue(
            e.size == a.size && e.indices.all { e[it] == a[it] },
            "expected ${e.toList()}, actual ${a.toList()}",
        )
    }

    private fun <T : IOType> assertDifferentOutput(before: Batch<T>, after: Batch<T>) {
        val b = before.value.toFloatArray()
        val a = after.value.toFloatArray()
        assertTrue(
            b.indices.any { abs(b[it] - a[it]) > 1e-6f },
            "output did not change: ${b.toList()}",
        )
    }
}

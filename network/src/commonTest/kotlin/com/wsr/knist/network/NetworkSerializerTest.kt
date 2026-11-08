@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.converter.raw.RawD2
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.sgd.Sgd
import com.wsr.knist.network.output.mean.meanSquare
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.function.relu.reLU
import com.wsr.knist.network.process.compute.padding.padding
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import okio.Buffer

class NetworkSerializerTest {

    private fun createNetwork(): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = Network.create(
        port = port(RawD1(3)),
        optimizer = Sgd(scheduler = Scheduler.Fix(rate = 0.01f)),
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.affine(neuron = 4).reLU().affine(neuron = 2).meanSquare()
    }

    private val input = Batch.of(IOType.d1(1f, 2f, 3f))

    private fun createPaddingNetwork(): Network.Src1.Sink1<Batch<IOType.D2>, Batch<IOType.D2>> = Network.create(
        port = port(RawD2(2, 3)),
        optimizer = Sgd(scheduler = Scheduler.Fix(rate = 0.01f)),
        initializer = Fixed(0.5f),
    ) { builder ->
        builder.padding(axis = 1, left = 1, right = 2).meanSquare()
    }

    private val paddingInput = Batch.of(IOType.d2(2, 3) { i, j -> i * 3f + j })

    @Test
    fun `JSON=文字列にシリアライズして復元できる`() = networkTestRule {
        val original = createNetwork()
        val restored = Network.Src1.Sink1.fromJson<Batch<IOType.D1>, Batch<IOType.D1>>(original.toJson())

        runTest {
            assertContentEquals(expected = original.expect(input), actual = restored.expect(input))
        }
    }

    @Test
    fun `JSON=BufferedSinkにシリアライズして復元できる`() = networkTestRule {
        val original = createNetwork()
        val buffer = Buffer()
        original.toJson(buffer)
        val restored = Network.Src1.Sink1.fromJson<Batch<IOType.D1>, Batch<IOType.D1>>(buffer)

        runTest {
            assertContentEquals(expected = original.expect(input), actual = restored.expect(input))
        }
    }

    @Test
    fun `CBOR=ByteArrayにシリアライズして復元できる`() = networkTestRule {
        val original = createNetwork()
        val restored = Network.Src1.Sink1.fromCbor<Batch<IOType.D1>, Batch<IOType.D1>>(original.toCbor())

        runTest {
            assertContentEquals(expected = original.expect(input), actual = restored.expect(input))
        }
    }

    @Test
    fun `CBOR=BufferedSinkにシリアライズして復元できる`() = networkTestRule {
        val original = createNetwork()
        val buffer = Buffer()
        original.toCbor(buffer)
        val restored = Network.Src1.Sink1.fromCbor<Batch<IOType.D1>, Batch<IOType.D1>>(buffer)

        runTest {
            assertContentEquals(expected = original.expect(input), actual = restored.expect(input))
        }
    }

    @Test
    fun `padding層を含むモデルもシリアライズして復元できる`() = networkTestRule {
        val original = createPaddingNetwork()
        val restored = Network.Src1.Sink1.fromCbor<Batch<IOType.D2>, Batch<IOType.D2>>(original.toCbor())

        runTest {
            assertContentEquals(expected = original.expect(paddingInput), actual = restored.expect(paddingInput))
        }
    }

    @Test
    fun `CBOR=JSONよりバイトサイズが小さい`() = networkTestRule {
        val network = createNetwork()
        val jsonSize = network.toJson().encodeToByteArray().size
        val cborSize = network.toCbor().size

        assertTrue(
            actual = cborSize < jsonSize,
            message = "CBOR ($cborSize bytes) should be smaller than JSON ($jsonSize bytes)",
        )
    }
}

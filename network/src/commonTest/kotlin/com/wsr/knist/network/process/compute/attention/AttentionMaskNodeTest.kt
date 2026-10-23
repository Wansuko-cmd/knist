@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network.process.compute.attention

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.Network
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.networkTestRule
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.sgd.Sgd
import com.wsr.knist.network.output.mean.meanSquare
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.function.linear.linear
import com.wsr.knist.network.process.reshape.token.tokenEmbedding
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class AttentionMaskNodeTest {
    @Test
    fun `mask=主系列外の派生ノードを渡してもグラフに含まれて計算される`() = networkTestRule {
        val network = Network.create(
            port = port(RawD1(4)),
            optimizer = Sgd(scheduler = Scheduler.Fix(rate = 0.01f)),
            initializer = Fixed(0.5f),
        ) { input ->
            input.tokenEmbedding(vocabSize = 3, tokenSize = 4)
                .attention(numOfHeads = 2, biases = { mask(input.linear(), 0f) })
                .meanSquare()
        }

        runTest {
            val actual = network.expect(Batch(1) { IOType.d1(0f, 1f, 2f, 1f) })

            assertEquals(expected = listOf(4, 4), actual = actual.shape)
        }
    }
}

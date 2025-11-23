@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.attention

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
class AttentionD2Test {
    @Test
    fun `AttentionD2の_expect=Multi-Head Attentionの出力形状が正しい`() {
        // channel=4, inputY=8, numOfHeads=2, dk=4
        val numOfHeads = 2
        val channel = 4
        val inputY = 8
        val dim = inputY / numOfHeads

        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightK = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightV = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerK = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerV = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerO = Sgd(0.01f).d2(numOfHeads * dim, inputY),
        )

        // 入力: [batch=2, channel=4, inputY=8]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() },
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 2).toFloat() },
        )
        // Contextには元の入力(ダミーのD1)を渡す
        val originalInput = batchOf(
            IOType.d1(channel) { it.toFloat() },
            IOType.d1(channel) { it.toFloat() },
        )
        val context = Context(originalInput)

        val result = attention._expect(input, context) as Batch<IOType.D2>
        // 出力形状の確認
        assertEquals(expected = 2, actual = result.size)
        val output = result[0]
        assertEquals(expected = channel, actual = output.shape[0])
        assertEquals(expected = inputY, actual = output.shape[1])
    }

    @Test
    fun `AttentionD2の_train=逆伝播が動作する`() {
        // シンプルなケース: channel=2, inputY=4, numOfHeads=2, dk=2
        val numOfHeads = 2
        val channel = 2
        val inputY = 4
        val dim = inputY / numOfHeads

        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightK = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightV = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerK = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerV = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerO = Sgd(0.01f).d2(numOfHeads * dim, inputY),
        )

        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // deltaは全て1.0を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        val result = attention._train(input, context, calcDelta) as Batch<IOType.D2>
        // 入力への勾配の形状を確認
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = channel, actual = dx.shape[0])
        assertEquals(expected = inputY, actual = dx.shape[1])
    }

    @Test
    fun `AttentionD2の_train=重みが更新される`() {
        // channel=2, inputY=4, numOfHeads=2, dk=2
        val numOfHeads = 2
        val channel = 2
        val inputY = 4
        val dim = inputY / numOfHeads

        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            dim = dim,
            numOfHeads = numOfHeads,
            weightQ = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 1.0f }
            },
            weightK = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 1.0f }
            },
            weightV = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 1.0f }
            },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 1.0f },
            optimizerQ = List(numOfHeads) { Sgd(0.1f).d2(inputY, dim) },
            optimizerK = List(numOfHeads) { Sgd(0.1f).d2(inputY, dim) },
            optimizerV = List(numOfHeads) { Sgd(0.1f).d2(inputY, dim) },
            optimizerO = Sgd(0.1f).d2(numOfHeads * dim, inputY),
        )

        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.01f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // 更新前の出力を保存
        val beforeOutput = attention._expect(input, context) as Batch<IOType.D2>
        val beforeValue = beforeOutput[0][0, 0]

        // deltaは全て1.0を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        // 訓練を実行（重みが更新される）
        attention._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後の出力
        val afterOutput = attention._expect(input, context) as Batch<IOType.D2>
        val afterValue = afterOutput[0][0, 0]

        // 重みが更新されたことを確認（出力が変わっている）
        assertTrue(
            beforeValue != afterValue,
            "重みが更新されていません。before=$beforeValue, after=$afterValue",
        )
    }

    @Test
    fun `AttentionD2の_expect=numOfHeads=1のケースでも動作する（Single-Head）`() {
        // Single-Head Attention
        val numOfHeads = 1
        val channel = 3
        val inputY = 6
        val dim = inputY / numOfHeads

        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightK = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightV = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerK = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerV = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerO = Sgd(0.01f).d2(numOfHeads * dim, inputY),
        )

        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        val result = attention._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = channel, actual = output.shape[0])
        assertEquals(expected = inputY, actual = output.shape[1])
    }

    @Test
    fun `AttentionD2の_expect=numOfHeads=4のケースでも動作する`() {
        // 4-head Attention
        val numOfHeads = 4
        val channel = 3
        val inputY = 8
        val dim = inputY / numOfHeads

        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightK = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightV = List(numOfHeads) {
                IOType.d2(inputY, dim) { _, _ -> 0.1f }
            },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerK = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerV = List(numOfHeads) { Sgd(0.01f).d2(inputY, dim) },
            optimizerO = Sgd(0.01f).d2(numOfHeads * dim, inputY),
        )

        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        val result = attention._expect(input, context)
        assertEquals(expected = 1, actual = result.size)
        val output = result as Batch<IOType.D2>
        assertEquals(expected = channel, actual = output.shape[0])
        assertEquals(expected = inputY, actual = output.shape[1])
    }
}

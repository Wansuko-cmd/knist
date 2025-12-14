@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.attention

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.process.Context
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class AttentionD2Test {
    @Test
    fun `AttentionD2のexpect=MultiHeadで注目を計算する`() {
        val numOfHeads = 2
        val channel = 2
        val inputY = 4
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        val result = attention._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]

        // Test Case 1からの期待値
        assertEquals(expected = 0.040000003f, actual = output[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.040000003f, actual = output[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.040000003f, actual = output[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.040000003f, actual = output[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.07294103f, actual = output[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.07294103f, actual = output[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.07294103f, actual = output[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.07294103f, actual = output[1, 3], absoluteTolerance = 1e-5f)
    }

    @Test
    fun `AttentionD2のexpect=SingleHeadで注目を計算する`() {
        val numOfHeads = 1
        val channel = 3
        val inputY = 6
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, 0.2, 0.3, 0.4, 0.5, 0.6], [0.7, 0.8, 0.9, 1.0, 1.1, 1.2], [1.3, 1.4, 1.5, 1.6, 1.7, 1.8]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        val result = attention._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]

        // Test Case 2からの期待値
        assertEquals(expected = 0.126f, actual = output[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.126f, actual = output[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.126f, actual = output[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.126f, actual = output[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.126f, actual = output[0, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.126f, actual = output[0, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.26058498f, actual = output[1, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44853115f, actual = output[2, 5], absoluteTolerance = 1e-5f)
    }

    @Test
    fun `AttentionD2のtrain=入力を元に勾配計算を行う`() {
        val numOfHeads = 2
        val channel = 2
        val inputY = 4
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // deltaは全て1.0fを返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        val result = attention._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0]

        // 修正後の正しい期待値
        assertEquals(expected = 0.23529622f, actual = delta[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.23529622f, actual = delta[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.23529622f, actual = delta[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.23529622f, actual = delta[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.08615069f, actual = delta[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.08615069f, actual = delta[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.08615069f, actual = delta[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.08615069f, actual = delta[1, 3], absoluteTolerance = 1e-5f)
    }

    @Test
    fun `AttentionD2のtrain=SingleHeadで勾配計算を行う`() {
        val numOfHeads = 1
        val channel = 3
        val inputY = 6
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, 0.2, 0.3, 0.4, 0.5, 0.6], [0.7, 0.8, 0.9, 1.0, 1.1, 1.2], [1.3, 1.4, 1.5, 1.6, 1.7, 1.8]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // deltaは全て1.0fを返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        val result = attention._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0]

        // 修正後の正しい期待値
        assertEquals(expected = 0.44360238f, actual = delta[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44360238f, actual = delta[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44360238f, actual = delta[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44360238f, actual = delta[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44360238f, actual = delta[0, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.44360238f, actual = delta[0, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.35141447f, actual = delta[1, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.36751217f, actual = delta[2, 5], absoluteTolerance = 1e-5f)
    }

    @Test
    fun `AttentionD2のtrain=入力から重みの更新を行う`() {
        val numOfHeads = 2
        val channel = 2
        val inputY = 4
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // deltaは全て1.0fを返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        // trainで重みを更新
        attention._train(input, context, calcDelta)

        // 更新後のexpect結果を確認
        val afterOutput = attention._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = afterOutput.size)

        val output = afterOutput[0]
        // 修正後の正しい期待値
        assertEquals(expected = 0.03761759f, actual = output[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.03761759f, actual = output[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.03761759f, actual = output[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.03761759f, actual = output[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.06872426f, actual = output[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.06872426f, actual = output[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.06872426f, actual = output[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.06872426f, actual = output[1, 3], absoluteTolerance = 1e-5f)
    }

    @Test
    fun `AttentionD2のtrain=4 headsで重みの更新を行う`() {
        val numOfHeads = 4
        val channel = 3
        val inputY = 8
        val dim = inputY / numOfHeads

        // weightQ, weightK, weightV, weightO = 0.1f
        val attention = AttentionD2(
            outputX = channel,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightK = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightV = IOType.d2(inputY, numOfHeads * dim) { _, _ -> 0.1f },
            weightO = IOType.d2(numOfHeads * dim, inputY) { _, _ -> 0.1f },
            optimizerQ = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerK = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerV = Sgd(Scheduler.Fix(0.01f)).d2(inputY, numOfHeads * dim),
            optimizerO = Sgd(Scheduler.Fix(0.01f)).d2(numOfHeads * dim, inputY),
        )

        // input = [[0.1, ..., 0.8], [0.9, ..., 1.6], [1.7, ..., 2.4]]
        val input = batchOf(
            IOType.d2(channel, inputY) { x, y -> (x * inputY + y + 1).toFloat() * 0.1f },
        )
        val originalInput = batchOf(IOType.d1(channel) { it.toFloat() })
        val context = Context(originalInput)

        // deltaは全て1.0fを返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(channel, inputY) { _, _ -> 1.0f })
        }

        // trainで重みを更新
        attention._train(input, context, calcDelta)

        // 更新後のexpect結果を確認
        val afterOutput = attention._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = afterOutput.size)

        val output = afterOutput[0]
        // 修正後の正しい期待値
        assertEquals(expected = 0.14962749f, actual = output[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 6], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.14962749f, actual = output[0, 7], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 6], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.34262684f, actual = output[1, 7], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 2], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 3], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 4], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 5], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 6], absoluteTolerance = 1e-5f)
        assertEquals(expected = 0.6226538f, actual = output[2, 7], absoluteTolerance = 1e-5f)
    }
}

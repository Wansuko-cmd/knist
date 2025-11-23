@file:Suppress("UNCHECKED_CAST")

package com.wsr.layer.process.norm.layer.d1

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import com.wsr.core.set
import com.wsr.batch.toBatch
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD1Test {
    @Test
    fun `LayerNormD1の_expect=Layer正規化を適用`() {
        // weight = [1, 1]
        val weight = IOType.Companion.d1(listOf(1.0f, 1.0f))
        val norm =
            LayerNormD1(
                outputSize = 2,
                e = 1e-6f,
                optimizer = Sgd(0.1f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // 2つのバッチ: [2, 4], [1, 3]
        val input =
            batchOf(
                IOType.Companion.d1(listOf(2.0f, 4.0f)),
                IOType.Companion.d1(listOf(1.0f, 3.0f)),
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D1>
        // バッチ1: mean=3, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // output = [-1, 1] / sqrt(1+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0]
        val expectedStd1 = sqrt(1.0f + 1e-10f)
        assertEquals(
            expected = (-1.0f / expectedStd1),
            actual = output1[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.0f / expectedStd1),
            actual = output1[1],
            absoluteTolerance = 1e-4f,
        )

        // バッチ2: mean=2, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // output = [-1, 1] / sqrt(1+1e-10)
        val output2 = result[1]
        val expectedStd2 = sqrt(1.0f + 1e-10f)
        assertEquals(
            expected = (-1.0f / expectedStd2),
            actual = output2[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.0f / expectedStd2),
            actual = output2[1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD1の_train=weightが更新される`() {
        // weight = [2, 2]
        val weight = IOType.Companion.d1(listOf(2.0f, 2.0f))
        val norm =
            LayerNormD1(
                outputSize = 2,
                e = 1e-6f,
                optimizer = Sgd(0.1f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // 2つのバッチ: [0, 2], [2, 4]
        val input =
            batchOf(
                IOType.Companion.d1(listOf(0.0f, 2.0f)),
                IOType.Companion.d1(listOf(2.0f, 4.0f)),
            )
        val context = Context(input)

        // deltaは全て[1, 1]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { outputs ->
            (0 until outputs.size).map { IOType.d1(listOf(1.0f, 1.0f)) }.toBatch()
        }

        // バッチ1: mean=1, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // normalized1 = [-1, 1] / sqrt(1+1e-10) ≈ [-1, 1]
        // バッチ2: mean=3, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // normalized2 = [-1, 1] / sqrt(1+1e-10) ≈ [-1, 1]
        // normalized_avg = ([-1, 1] + [-1, 1]) / 2 = [-1, 1]
        // delta_avg = ([1, 1] + [1, 1]) / 2 = [1, 1]
        // dw = normalized_avg * delta_avg = [-1, 1] * [1, 1] = [-1, 1]
        // weight -= 0.1f * dw = [2, 2] - 0.1f * [-1, 1] = [2, 2] - [-0.1f, 0.1f] = [2.1f, 1.9f]

        // trainを実行
        norm._train(input, context, calcDelta) as Batch<IOType.D1>

        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(batchOf(input[0]), context) as Batch<IOType.D1>
        val expectedStd = sqrt(1.0f + 1e-10f)
        // output = [2.1f, 1.9f] * [-1, 1] / std
        assertEquals(
            expected = (2.1f * (-1.0f / expectedStd)),
            actual = afterOutput[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.9f * (1.0f / expectedStd)),
            actual = afterOutput[0][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD1の数値微分テスト=dxが計算されて返される`() {
        // weight = [1.5f, 2.0f, 1.0f]
        val weight = IOType.Companion.d1(listOf(1.5f, 2.0f, 1.0f))
        val norm =
            LayerNormD1(
                outputSize = 3,
                e = 1e-6f,
                optimizer = Sgd(0.01f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // [1, 4, 7]
        val input =
            batchOf(
                IOType.Companion.d1(listOf(1.0f, 4.0f, 7.0f)),
            )
        val context = Context(input)

        // deltaは[1, 0.5f, -1]を返す（任意の勾配）
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.Companion.d1(listOf(1.0f, 0.5f, -1.0f)))
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5f
        val numericalGradients = mutableListOf<Float>()

        for (i in 0 until 3) {
            // input[i]を少し増やす
            val inputPlus = input[0].value.toMutableList()
            inputPlus[i] += epsilon
            val outputPlus = norm._expect(batchOf(IOType.d1(inputPlus)), context) as Batch<IOType.D1>
            val lossPlus = calcLoss(outputPlus, calcDelta)

            // input[i]を少し減らす
            val inputMinus = input[0].value.toMutableList()
            inputMinus[i] -= epsilon
            val outputMinus = norm._expect(batchOf(IOType.d1(inputMinus)), context) as Batch<IOType.D1>
            val lossMinus = calcLoss(outputMinus, calcDelta)

            // 数値微分
            val gradient = ((lossPlus - lossMinus) / (2 * epsilon))
            numericalGradients.add(gradient)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, context, calcDelta) as Batch<IOType.D1>

        // 数値微分と実際の勾配を比較
        for (i in 0 until 3) {
            assertEquals(
                expected = numericalGradients[i],
                actual = dx[0][i],
                absoluteTolerance = 2e-2f,
                message = "要素[$i]の勾配が数値微分と一致しません",
            )
        }
    }

    /**
     * 損失関数（テスト用）
     * loss = Σ(output_i * delta_i)
     */
    private fun calcLoss(output: Batch<IOType>, calcDelta: (Batch<IOType>) -> Batch<IOType>): Float {
        val delta = calcDelta(output)as Batch<IOType.D1>
        val out = output as Batch<IOType.D1>
        var loss = 0.0f
        for (i in 0 until out.value.size) {
            loss += out[0][i] * delta[0][i]
        }
        return loss
    }
}

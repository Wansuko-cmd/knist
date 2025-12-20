@file:Suppress("UNCHECKED_CAST")

package com.wsr.process.compute.norm.layer.d2

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.batch.toBatch
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.process.Context
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormAxis0D2Test {
    @Test
    fun `LayerNormAxis0D2の_expect=axis0で正規化を適用`() {
        // weight = [[1, 1], [1, 1]]
        val weight = IOType.d2(2, 2) { _, _ -> 1.0f }
        val norm =
            LayerNormAxisD2(
                outputX = 2,
                outputY = 2,
                axis = 1,
                e = 1e-10f,
            )

        // 入力: [[1, 4], [3, 2]]
        // 列0: [1, 3], mean=2, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // 列1: [4, 2], mean=3, numerator=[1, -1], variance=1, std=sqrt(1+1e-10)
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 0 && y == 1 -> 4.0f
                        x == 1 && y == 0 -> 3.0f
                        else -> 2.0f
                    }
                },
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        val expectedStd = sqrt(1.0f + 1e-10f)

        // 列0の正規化: [-1, 1] / sqrt(1+1e-10)
        assertEquals(
            expected = (-1.0f / expectedStd),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.0f / expectedStd),
            actual = output[1, 0],
            absoluteTolerance = 1e-4f,
        )

        // 列1の正規化: [1, -1] / sqrt(1+1e-10)
        assertEquals(
            expected = (1.0f / expectedStd),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (-1.0f / expectedStd),
            actual = output[1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormAxis0D2の_train=weightが更新される`() {
        // weight = [[2, 2], [2, 2]]
        val weight = IOType.d2(2, 2) { _, _ -> 2.0f }
        val norm =
            LayerNormAxisD2(
                outputX = 2,
                outputY = 2,
                axis = 1,
                e = 1e-10f,
            )

        // 入力: [[1, 4], [3, 2]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 0 && y == 1 -> 4.0f
                        x == 1 && y == 0 -> 3.0f
                        else -> 2.0f
                    }
                },
            )
        val context = Context(input)

        // deltaは全て[[1, 1], [1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { outputs ->
            (0 until outputs.size).map { IOType.d2(2, 2) { _, _ -> 1.0f } }.toBatch()
        }

        val expectedStd = sqrt(1.0f + 1e-10f)
        // 列0の正規化: [-1, 1] / sqrt(1+1e-10)
        val normalized00 = -1.0f / expectedStd
        val normalized10 = 1.0f / expectedStd
        // 列1の正規化: [1, -1] / sqrt(1+1e-10)
        val normalized01 = 1.0f / expectedStd
        val normalized11 = -1.0f / expectedStd

        // dw = normalized * delta = [[normalized00, normalized01], [normalized10, normalized11]] * [[1, 1], [1, 1]]
        // weight -= 0.1f * dw

        // trainを実行
        norm._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果を確認
        val afterOutput = norm._expect(input, context) as Batch<IOType.D2>

        // weight更新: weight[0,0] = 2.0f - 0.1f * normalized00 = 2.0f + 0.1f / sqrt(1+1e-10)
        // output[0,0] = weight[0,0] * normalized00
        assertEquals(
            expected = ((2.0f - 0.1f * normalized00) * normalized00),
            actual = afterOutput[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized01) * normalized01),
            actual = afterOutput[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized10) * normalized10),
            actual = afterOutput[0][1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized11) * normalized11),
            actual = afterOutput[0][1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormAxis0D2の数値微分テスト=dxが計算されて返される`() {
        // weight = [[1.5f, 2.0f], [1.0f, 0.8f]]
        val weight =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.5f
                    x == 0 && y == 1 -> 2.0f
                    x == 1 && y == 0 -> 1.0f
                    else -> 0.8f
                }
            }
        val norm =
            LayerNormAxisD2(
                outputX = 2,
                outputY = 2,
                axis = 1,
                e = 1e-10f,
            )

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[1, 0.5f], [-1, 0.8f]]を返す（任意の勾配）
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 0 && y == 1 -> 0.5f
                        x == 1 && y == 0 -> -1.0f
                        else -> 0.8f
                    }
                },
            )
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5f
        val numericalGradients = mutableListOf<List<Float>>()

        for (i in 0 until 2) {
            val row = mutableListOf<Float>()
            for (j in 0 until 2) {
                // input[i, j]を少し増やす
                val inputPlus = input[0].value
                inputPlus[i * 2 + j] += epsilon
                val outputPlus = norm._expect(
                    batchOf(IOType.d2(listOf(2, 2), inputPlus.toFloatArray())),
                    context,
                ) as Batch<IOType.D2>
                val lossPlus = calcLoss(outputPlus, calcDelta)

                // input[i, j]を少し減らす
                val inputMinus = input[0].value
                inputMinus[i * 2 + j] -= epsilon
                val outputMinus = norm._expect(
                    batchOf(IOType.d2(listOf(2, 2), inputMinus.toFloatArray())),
                    context,
                ) as Batch<IOType.D2>
                val lossMinus = calcLoss(outputMinus, calcDelta)

                // 数値微分
                val gradient = ((lossPlus - lossMinus) / (2 * epsilon))
                row.add(gradient)
            }
            numericalGradients.add(row)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, context, calcDelta) as Batch<IOType.D2>

        // 数値微分と実際の勾配を比較
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(
                    expected = numericalGradients[i][j],
                    actual = dx[0][i, j],
                    absoluteTolerance = 1e-3f,
                    message = "要素[$i, $j]の勾配が数値微分と一致しません",
                )
            }
        }
    }

    /**
     * 損失関数（テスト用）
     * loss = Σ(output_ij * delta_ij)
     */
    private fun calcLoss(output: Batch<IOType>, calcDelta: (Batch<IOType>) -> Batch<IOType>): Float {
        val delta = calcDelta(output) as Batch<IOType.D2>
        val out = output as Batch<IOType.D2>
        var loss = 0.0f
        for (i in 0 until out.shape[0]) {
            for (j in 0 until out.shape[1]) {
                loss += out[0][i, j] * delta[0][i, j]
            }
        }
        return loss
    }
}

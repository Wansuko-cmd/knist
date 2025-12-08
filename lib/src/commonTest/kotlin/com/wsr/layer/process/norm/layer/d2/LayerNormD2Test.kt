@file:Suppress("UNCHECKED_CAST")

package com.wsr.layer.process.norm.layer.d2

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.batch.toBatch
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD2Test {
    @Test
    fun `LayerNormD2の_expect=Layer正規化を適用`() {
        // weight = [[1, 1], [1, 1]]
        val weight = IOType.d2(2, 2) { _, _ -> 1.0f }
        val norm =
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(i = 2, j = 2),
                weight = weight,
            )

        // 2つのバッチ: [[0, 2], [2, 4]], [[2, 4], [4, 6]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2 + 2).toFloat() },
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D2>
        // バッチ1: [[0, 2], [2, 4]], mean=2, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // output = [[-2, 0], [0, 2]] / sqrt(2+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0]
        val expectedStd1 = sqrt(2.0f + 1e-10f)
        assertEquals(
            expected = (-2.0f / expectedStd1),
            actual = output1[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd1),
            actual = output1[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd1),
            actual = output1[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd1),
            actual = output1[1, 1],
            absoluteTolerance = 1e-4f,
        )

        // バッチ2: [[2, 4], [4, 6]], mean=4, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // output = [[-2, 0], [0, 2]] / sqrt(2+1e-10)
        val output2 = result[1] as IOType.D2
        val expectedStd2 = sqrt(2.0f + 1e-10f)
        assertEquals(
            expected = (-2.0f / expectedStd2),
            actual = output2[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd2),
            actual = output2[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd2),
            actual = output2[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd2),
            actual = output2[1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD2の_train=weightが更新される`() {
        // weight = [[2, 2], [2, 2]]
        val weight = IOType.d2(2, 2) { _, _ -> 2.0f }
        val norm =
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(i = 2, j = 2),
                weight = weight,
            )

        // 2つのバッチ: [[0, 2], [2, 4]], [[2, 4], [4, 6]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2 + 2).toFloat() },
            )
        val context = Context(input)

        // deltaは全て[[1, 1], [1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { outputs ->
            (0 until outputs.size).map { IOType.d2(2, 2) { _, _ -> 1.0f } }.toBatch()
        }

        // バッチ1: mean=2, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // normalized1 = [[-2, 0], [0, 2]] / sqrt(2+1e-10) ≈ [[-1.414f, 0], [0, 1.414f]]
        // バッチ2: mean=4, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // normalized2 = [[-2, 0], [0, 2]] / sqrt(2+1e-10) ≈ [[-1.414f, 0], [0, 1.414f]]
        // normalized_avg = (normalized1 + normalized2) / 2 = [[-1.414f, 0], [0, 1.414f]]
        // delta_avg = [[1, 1], [1, 1]]
        // dw = normalized_avg * delta_avg = [[-1.414f, 0], [0, 1.414f]]
        // weight -= 0.1f * dw = [[2, 2], [2, 2]] - 0.1f * [[-1.414f, 0], [0, 1.414f]]
        //        = [[2, 2], [2, 2]] - [[-0.1414f, 0], [0, 0.1414f]]
        //        = [[2.1414f, 2], [2, 1.8586f]]

        // trainを実行
        norm._train(input, context, calcDelta) as Batch<IOType.D2>

        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(batchOf(input[0]), context) as Batch<IOType.D2>
        val expectedStd = sqrt(2.0f + 1e-10f)
        // output = [[2.1414f, 2], [2, 1.8586f]] * [[-2, 0], [0, 2]] / std
        assertEquals(
            expected = ((2.0f + 0.1f * 2.0f / expectedStd) * (-2.0f / expectedStd)),
            actual = afterOutput[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f * (0.0f / expectedStd)),
            actual = afterOutput[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f * (0.0f / expectedStd)),
            actual = afterOutput[0][1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * 2.0f / expectedStd) * (2.0f / expectedStd)),
            actual = afterOutput[0][1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD2の数値微分テスト=dxが計算されて返される`() {
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
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(Scheduler.Fix(0.01f)).d2(i = 2, j = 2),
                weight = weight,
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
                    absoluteTolerance = 3e-2f,
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

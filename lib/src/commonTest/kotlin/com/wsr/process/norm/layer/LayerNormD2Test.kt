@file:Suppress("NonAsciiCharacters")

package com.wsr.process.norm.layer

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD2Test {
    @Test
    fun `LayerNormD2の_expect=Layer正規化を適用`() {
        // weight = [[1, 1], [1, 1]]
        val weight = IOType.d2(2, 2) { _, _ -> 1.0 }
        val norm =
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = weight,
            )

        // 2つのバッチ: [[0, 2], [2, 4]], [[2, 4], [4, 6]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2).toDouble() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2 + 2).toDouble() },
            )

        val result = norm._expect(input)

        // バッチ1: [[0, 2], [2, 4]], mean=2, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // output = [[-2, 0], [0, 2]] / sqrt(2+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0] as IOType.D2
        val expectedStd1 = sqrt(2.0 + 1e-10)
        assertEquals(expected = -2.0 / expectedStd1, actual = output1[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.0 / expectedStd1, actual = output1[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.0 / expectedStd1, actual = output1[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 / expectedStd1, actual = output1[1, 1], absoluteTolerance = 1e-4)

        // バッチ2: [[2, 4], [4, 6]], mean=4, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // output = [[-2, 0], [0, 2]] / sqrt(2+1e-10)
        val output2 = result[1] as IOType.D2
        val expectedStd2 = sqrt(2.0 + 1e-10)
        assertEquals(expected = -2.0 / expectedStd2, actual = output2[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.0 / expectedStd2, actual = output2[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.0 / expectedStd2, actual = output2[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 / expectedStd2, actual = output2[1, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `LayerNormD2の_train=weightが更新される`() {
        // weight = [[2, 2], [2, 2]]
        val weight = IOType.d2(2, 2) { _, _ -> 2.0 }
        val norm =
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = weight,
            )

        // 2つのバッチ: [[0, 2], [2, 4]], [[2, 4], [4, 6]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2).toDouble() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y * 2 + 2).toDouble() },
            )

        // deltaは全て[[1, 1], [1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = { outputs ->
            outputs.map { IOType.d2(2, 2) { _, _ -> 1.0 } }
        }

        // バッチ1: mean=2, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // normalized1 = [[-2, 0], [0, 2]] / sqrt(2+1e-10) ≈ [[-1.414, 0], [0, 1.414]]
        // バッチ2: mean=4, numerator=[[-2, 0], [0, 2]], variance=2, std=sqrt(2+1e-10)
        // normalized2 = [[-2, 0], [0, 2]] / sqrt(2+1e-10) ≈ [[-1.414, 0], [0, 1.414]]
        // normalized_avg = (normalized1 + normalized2) / 2 = [[-1.414, 0], [0, 1.414]]
        // delta_avg = [[1, 1], [1, 1]]
        // dw = normalized_avg * delta_avg = [[-1.414, 0], [0, 1.414]]
        // weight -= 0.1 * dw = [[2, 2], [2, 2]] - 0.1 * [[-1.414, 0], [0, 1.414]]
        //        = [[2, 2], [2, 2]] - [[-0.1414, 0], [0, 0.1414]]
        //        = [[2.1414, 2], [2, 1.8586]]

        // trainを実行
        try {
            norm._train(input, calcDelta)
        } catch (e: NotImplementedError) {
            // dxの計算が未実装なので例外をキャッチ
        }

        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(listOf(input[0]))[0] as IOType.D2
        val expectedStd = sqrt(2.0 + 1e-10)
        // output = [[2.1414, 2], [2, 1.8586]] * [[-2, 0], [0, 2]] / std
        assertEquals(expected = (2.0 + 0.1 * 2.0 / expectedStd) * (-2.0 / expectedStd), actual = afterOutput[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 * (0.0 / expectedStd), actual = afterOutput[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 * (0.0 / expectedStd), actual = afterOutput[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = (2.0 - 0.1 * 2.0 / expectedStd) * (2.0 / expectedStd), actual = afterOutput[1, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `LayerNormD2の数値微分テスト=dxが計算されて返される`() {
        // weight = [[1.5, 2.0], [1.0, 0.8]]
        val weight =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.5
                    x == 0 && y == 1 -> 2.0
                    x == 1 && y == 0 -> 1.0
                    else -> 0.8
                }
            }
        val norm =
            LayerNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.01).d2(x = 2, y = 2),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[[1, 0.5], [-1, 0.8]]を返す（任意の勾配）
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0
                        x == 0 && y == 1 -> 0.5
                        x == 1 && y == 0 -> -1.0
                        else -> 0.8
                    }
                },
            )
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5
        val numericalGradients = mutableListOf<List<Double>>()

        for (i in 0 until 2) {
            val row = mutableListOf<Double>()
            for (j in 0 until 2) {
                // input[i, j]を少し増やす
                val inputPlus = input[0].value.copyOf()
                inputPlus[i * 2 + j] += epsilon
                val outputPlus = norm._expect(listOf(IOType.d2(listOf(2, 2), inputPlus.toList())))
                val lossPlus = calcLoss(outputPlus, calcDelta)

                // input[i, j]を少し減らす
                val inputMinus = input[0].value.copyOf()
                inputMinus[i * 2 + j] -= epsilon
                val outputMinus = norm._expect(listOf(IOType.d2(listOf(2, 2), inputMinus.toList())))
                val lossMinus = calcLoss(outputMinus, calcDelta)

                // 数値微分
                val gradient = (lossPlus - lossMinus) / (2 * epsilon)
                row.add(gradient)
            }
            numericalGradients.add(row)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, calcDelta)[0] as IOType.D2

        // 数値微分と実際の勾配を比較
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(
                    expected = numericalGradients[i][j],
                    actual = dx[i, j],
                    absoluteTolerance = 1e-3,
                    message = "要素[$i, $j]の勾配が数値微分と一致しません",
                )
            }
        }
    }

    /**
     * 損失関数（テスト用）
     * loss = Σ(output_ij * delta_ij)
     */
    private fun calcLoss(
        output: List<IOType>,
        calcDelta: (List<IOType>) -> List<IOType>,
    ): Double {
        val delta = calcDelta(output)[0] as IOType.D2
        val out = output[0] as IOType.D2
        var loss = 0.0
        for (i in 0 until out.shape[0]) {
            for (j in 0 until out.shape[1]) {
                loss += out[i, j] * delta[i, j]
            }
        }
        return loss
    }
}

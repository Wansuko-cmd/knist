package com.wsr.layer.process.norm.layer.d2

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormAxis1D2Test {
    @Test
    fun `LayerNormAxis1D2の_expect=axis1で正規化を適用`() {
        // weight = [[1, 1], [1, 1]]
        val weight = IOType.Companion.d2(2, 2) { _, _ -> 1.0f }
        val norm =
            LayerNormAxis1D2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(0.1f).d2(x = 2, y = 2),
                weight = weight,
            )

        // 入力: [[1, 3], [2, 4]]
        // 行0: [1, 3], mean=2, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // 行1: [2, 4], mean=3, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 0 && y == 1 -> 3.0f
                        x == 1 && y == 0 -> 2.0f
                        else -> 4.0f
                    }
                },
            )

        val result = norm._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        val expectedStd = sqrt(1.0f + 1e-10f)

        // 行0の正規化: [-1, 1] / sqrt(1+1e-10)
        assertEquals(
            expected = (-1.0f / expectedStd),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.0f / expectedStd),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )

        // 行1の正規化: [-1, 1] / sqrt(1+1e-10)
        assertEquals(
            expected = (-1.0f / expectedStd),
            actual = output[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (1.0f / expectedStd),
            actual = output[1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormAxis1D2の_train=weightが更新される`() {
        // weight = [[2, 2], [2, 2]]
        val weight = IOType.Companion.d2(2, 2) { _, _ -> 2.0f }
        val norm =
            LayerNormAxis1D2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(0.1f).d2(x = 2, y = 2),
                weight = weight,
            )

        // 入力: [[1, 3], [2, 4]]
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 0 && y == 1 -> 3.0f
                        x == 1 && y == 0 -> 2.0f
                        else -> 4.0f
                    }
                },
            )

        // deltaは全て[[1, 1], [1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = { outputs ->
            outputs.map { IOType.Companion.d2(2, 2) { _, _ -> 1.0f } }
        }

        val expectedStd = sqrt(1.0f + 1e-10f)
        // 両行とも正規化: [-1, 1] / sqrt(1+1e-10)
        val normalized0 = -1.0f / expectedStd
        val normalized1 = 1.0f / expectedStd

        // trainを実行
        norm._train(input, calcDelta)

        // 更新後のexpect結果を確認
        val afterOutput = norm._expect(input)[0] as IOType.D2

        // weight更新後の出力を検証
        assertEquals(
            expected = ((2.0f - 0.1f * normalized0) * normalized0),
            actual = afterOutput[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized1) * normalized1),
            actual = afterOutput[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized0) * normalized0),
            actual = afterOutput[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = ((2.0f - 0.1f * normalized1) * normalized1),
            actual = afterOutput[1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormAxis1D2の数値微分テスト=dxが計算されて返される`() {
        // weight = [[1.5f, 2.0f], [1.0f, 0.8f]]
        val weight =
            IOType.Companion.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.5f
                    x == 0 && y == 1 -> 2.0f
                    x == 1 && y == 0 -> 1.0f
                    else -> 0.8f
                }
            }
        val norm =
            LayerNormAxis1D2(
                outputX = 2,
                outputY = 2,
                e = 1e-10f,
                optimizer = Sgd(0.01f).d2(x = 2, y = 2),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )

        // deltaは[[1, 0.5f], [-1, 0.8f]]を返す（任意の勾配）
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
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
                val inputPlus = input[0].value.copyOf()
                inputPlus[i * 2 + j] += epsilon
                val outputPlus = norm._expect(listOf(IOType.Companion.d2(listOf(2, 2), inputPlus.toList())))
                val lossPlus = calcLoss(outputPlus, calcDelta)

                // input[i, j]を少し減らす
                val inputMinus = input[0].value.copyOf()
                inputMinus[i * 2 + j] -= epsilon
                val outputMinus = norm._expect(listOf(IOType.Companion.d2(listOf(2, 2), inputMinus.toList())))
                val lossMinus = calcLoss(outputMinus, calcDelta)

                // 数値微分
                val gradient = ((lossPlus - lossMinus) / (2 * epsilon))
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
    private fun calcLoss(output: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): Float {
        val delta = calcDelta(output)[0] as IOType.D2
        val out = output[0] as IOType.D2
        var loss = 0.0f
        for (i in 0 until out.shape[0]) {
            for (j in 0 until out.shape[1]) {
                loss += out[i, j] * delta[i, j]
            }
        }
        return loss
    }
}

package com.wsr.layer.process.norm.layer.d2

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormAxis0D2Test {
    @Test
    fun `LayerNormAxis0D2の_expect=axis0で正規化を適用`() {
        // weight = [[1, 1], [1, 1]]
        val weight = IOType.Companion.d2(2, 2) { _, _ -> 1.0 }
        val norm =
            LayerNormAxis0D2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = weight,
            )

        // 入力: [[1, 4], [3, 2]]
        // 列0: [1, 3], mean=2, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // 列1: [4, 2], mean=3, numerator=[1, -1], variance=1, std=sqrt(1+1e-10)
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0
                        x == 0 && y == 1 -> 4.0
                        x == 1 && y == 0 -> 3.0
                        else -> 2.0
                    }
                },
            )

        val result = norm._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        val expectedStd = sqrt(1.0 + 1e-10)

        // 列0の正規化: [-1, 1] / sqrt(1+1e-10)
        assertEquals(
            expected = -1.0 / expectedStd,
            actual = output[0, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 1.0 / expectedStd,
            actual = output[1, 0],
            absoluteTolerance = 1e-4
        )

        // 列1の正規化: [1, -1] / sqrt(1+1e-10)
        assertEquals(
            expected = 1.0 / expectedStd,
            actual = output[0, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = -1.0 / expectedStd,
            actual = output[1, 1],
            absoluteTolerance = 1e-4
        )
    }

    @Test
    fun `LayerNormAxis0D2の_train=weightが更新される`() {
        // weight = [[2, 2], [2, 2]]
        val weight = IOType.Companion.d2(2, 2) { _, _ -> 2.0 }
        val norm =
            LayerNormAxis0D2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = weight,
            )

        // 入力: [[1, 4], [3, 2]]
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0
                        x == 0 && y == 1 -> 4.0
                        x == 1 && y == 0 -> 3.0
                        else -> 2.0
                    }
                },
            )

        // deltaは全て[[1, 1], [1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = { outputs ->
            outputs.map { IOType.Companion.d2(2, 2) { _, _ -> 1.0 } }
        }

        val expectedStd = sqrt(1.0 + 1e-10)
        // 列0の正規化: [-1, 1] / sqrt(1+1e-10)
        val normalized00 = -1.0 / expectedStd
        val normalized10 = 1.0 / expectedStd
        // 列1の正規化: [1, -1] / sqrt(1+1e-10)
        val normalized01 = 1.0 / expectedStd
        val normalized11 = -1.0 / expectedStd

        // dw = normalized * delta = [[normalized00, normalized01], [normalized10, normalized11]] * [[1, 1], [1, 1]]
        // weight -= 0.1 * dw

        // trainを実行
        norm._train(input, calcDelta)

        // 更新後のexpect結果を確認
        val afterOutput = norm._expect(input)[0] as IOType.D2

        // weight更新: weight[0,0] = 2.0 - 0.1 * normalized00 = 2.0 + 0.1 / sqrt(1+1e-10)
        // output[0,0] = weight[0,0] * normalized00
        assertEquals(
            expected = (2.0 - 0.1 * normalized00) * normalized00,
            actual = afterOutput[0, 0],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = (2.0 - 0.1 * normalized01) * normalized01,
            actual = afterOutput[0, 1],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = (2.0 - 0.1 * normalized10) * normalized10,
            actual = afterOutput[1, 0],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = (2.0 - 0.1 * normalized11) * normalized11,
            actual = afterOutput[1, 1],
            absoluteTolerance = 1e-4,
        )
    }

    @Test
    fun `LayerNormAxis0D2の数値微分テスト=dxが計算されて返される`() {
        // weight = [[1.5, 2.0], [1.0, 0.8]]
        val weight =
            IOType.Companion.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.5
                    x == 0 && y == 1 -> 2.0
                    x == 1 && y == 0 -> 1.0
                    else -> 0.8
                }
            }
        val norm =
            LayerNormAxis0D2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.01).d2(x = 2, y = 2),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[[1, 0.5], [-1, 0.8]]を返す（任意の勾配）
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.Companion.d2(2, 2) { x, y ->
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
                val outputPlus = norm._expect(listOf(IOType.Companion.d2(listOf(2, 2), inputPlus.toList())))
                val lossPlus = calcLoss(outputPlus, calcDelta)

                // input[i, j]を少し減らす
                val inputMinus = input[0].value.copyOf()
                inputMinus[i * 2 + j] -= epsilon
                val outputMinus = norm._expect(listOf(IOType.Companion.d2(listOf(2, 2), inputMinus.toList())))
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
    private fun calcLoss(output: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): Double {
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

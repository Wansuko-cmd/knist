package com.wsr.layer.process.norm.layer.d3

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD3Test {
    @Test
    fun `LayerNormD3の_expect=Layer正規化を適用`() {
        // weight = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        val weight = IOType.Companion.d3(2, 2, 2) { _, _, _ -> 1.0 }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1).d3(x = 2, y = 2, z = 2),
                weight = weight,
            )

        // 2つのバッチ: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], [[[2, 4], [4, 6]], [[6, 8], [8, 10]]]
        val input =
            listOf(
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2).toDouble() },
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2 + 2).toDouble() },
            )

        val result = norm._expect(input)

        // バッチ1: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], mean=4, numerator=[[[-4, -2], [-2, 0]], [[0, 2], [2, 4]]]
        // variance = (16 + 4 + 4 + 0 + 0 + 4 + 4 + 16) / 8 = 48 / 8 = 6, std=sqrt(6+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0] as IOType.D3
        val expectedStd1 = sqrt(6.0 + 1e-10)
        assertEquals(
            expected = -4.0 / expectedStd1,
            actual = output1[0, 0, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = -2.0 / expectedStd1,
            actual = output1[0, 0, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = -2.0 / expectedStd1,
            actual = output1[0, 1, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 0.0 / expectedStd1,
            actual = output1[0, 1, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 0.0 / expectedStd1,
            actual = output1[1, 0, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 2.0 / expectedStd1,
            actual = output1[1, 0, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 2.0 / expectedStd1,
            actual = output1[1, 1, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 4.0 / expectedStd1,
            actual = output1[1, 1, 1],
            absoluteTolerance = 1e-4
        )

        // バッチ2: [[[2, 4], [4, 6]], [[6, 8], [8, 10]]], mean=6, numerator=[[[-4, -2], [-2, 0]], [[0, 2], [2, 4]]]
        // variance = (16 + 4 + 4 + 0 + 0 + 4 + 4 + 16) / 8 = 48 / 8 = 6, std=sqrt(6+1e-10)
        val output2 = result[1] as IOType.D3
        val expectedStd2 = sqrt(6.0 + 1e-10)
        assertEquals(
            expected = -4.0 / expectedStd2,
            actual = output2[0, 0, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = -2.0 / expectedStd2,
            actual = output2[0, 0, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = -2.0 / expectedStd2,
            actual = output2[0, 1, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 0.0 / expectedStd2,
            actual = output2[0, 1, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 0.0 / expectedStd2,
            actual = output2[1, 0, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 2.0 / expectedStd2,
            actual = output2[1, 0, 1],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 2.0 / expectedStd2,
            actual = output2[1, 1, 0],
            absoluteTolerance = 1e-4
        )
        assertEquals(
            expected = 4.0 / expectedStd2,
            actual = output2[1, 1, 1],
            absoluteTolerance = 1e-4
        )
    }

    @Test
    fun `LayerNormD3の_train=weightが更新される`() {
        // weight = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]]
        val weight = IOType.Companion.d3(2, 2, 2) { _, _, _ -> 2.0 }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1).d3(x = 2, y = 2, z = 2),
                weight = weight,
            )

        // 2つのバッチ: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], [[[2, 4], [4, 6]], [[6, 8], [8, 10]]]
        val input =
            listOf(
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2).toDouble() },
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2 + 2).toDouble() },
            )

        // deltaは全て[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = { outputs ->
            outputs.map { IOType.Companion.d3(2, 2, 2) { _, _, _ -> 1.0 } }
        }

        val expectedStd = sqrt(6.0 + 1e-10)
        // normalized_avg ≈ [[[-1.633, -0.816], [-0.816, 0]], [[0, 0.816], [0.816, 1.633]]]
        // delta_avg = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        // dw = normalized_avg * delta_avg
        // weight -= 0.1 * dw

        // trainを実行
        norm._train(input, calcDelta)

        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(listOf(input[0]))[0] as IOType.D3

        // output = updated_weight * normalized
        // normalized[0, 0, 0] = -4.0 / expectedStd
        // updated_weight[0, 0, 0] = 2.0 - 0.1 * (-4.0 / expectedStd) = 2.0 + 0.4 / expectedStd
        assertEquals(
            expected = (2.0 + 0.1 * 4.0 / expectedStd) * (-4.0 / expectedStd),
            actual = afterOutput[0, 0, 0],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = (2.0 + 0.1 * 2.0 / expectedStd) * (-2.0 / expectedStd),
            actual = afterOutput[0, 0, 1],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = (2.0 - 0.1 * 4.0 / expectedStd) * (4.0 / expectedStd),
            actual = afterOutput[1, 1, 1],
            absoluteTolerance = 1e-4,
        )
    }

    @Test
    fun `LayerNormD3の数値微分テスト=dxが計算されて返される`() {
        // weight = [[[1.5, 2.0], [1.0, 0.8]], [[1.2, 0.9], [1.1, 1.3]]]
        val weight =
            IOType.Companion.d3(2, 2, 2) { x, y, z ->
                when {
                    x == 0 && y == 0 && z == 0 -> 1.5
                    x == 0 && y == 0 && z == 1 -> 2.0
                    x == 0 && y == 1 && z == 0 -> 1.0
                    x == 0 && y == 1 && z == 1 -> 0.8
                    x == 1 && y == 0 && z == 0 -> 1.2
                    x == 1 && y == 0 && z == 1 -> 0.9
                    x == 1 && y == 1 && z == 0 -> 1.1
                    else -> 1.3
                }
            }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.01).d3(x = 2, y = 2, z = 2),
                weight = weight,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[[[1, 0.5], [-1, 0.8]], [[0.7, -0.5], [0.9, 1.2]]]を返す（任意の勾配）
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.Companion.d3(2, 2, 2) { x, y, z ->
                    when {
                        x == 0 && y == 0 && z == 0 -> 1.0
                        x == 0 && y == 0 && z == 1 -> 0.5
                        x == 0 && y == 1 && z == 0 -> -1.0
                        x == 0 && y == 1 && z == 1 -> 0.8
                        x == 1 && y == 0 && z == 0 -> 0.7
                        x == 1 && y == 0 && z == 1 -> -0.5
                        x == 1 && y == 1 && z == 0 -> 0.9
                        else -> 1.2
                    }
                },
            )
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5
        val numericalGradients = mutableListOf<List<List<Double>>>()

        for (i in 0 until 2) {
            val plane = mutableListOf<List<Double>>()
            for (j in 0 until 2) {
                val row = mutableListOf<Double>()
                for (k in 0 until 2) {
                    // input[i, j, k]を少し増やす
                    val inputPlus = input[0].value.copyOf()
                    inputPlus[i * 4 + j * 2 + k] += epsilon
                    val outputPlus = norm._expect(listOf(IOType.Companion.d3(listOf(2, 2, 2), inputPlus.toList())))
                    val lossPlus = calcLoss(outputPlus, calcDelta)

                    // input[i, j, k]を少し減らす
                    val inputMinus = input[0].value.copyOf()
                    inputMinus[i * 4 + j * 2 + k] -= epsilon
                    val outputMinus = norm._expect(listOf(IOType.Companion.d3(listOf(2, 2, 2), inputMinus.toList())))
                    val lossMinus = calcLoss(outputMinus, calcDelta)

                    // 数値微分
                    val gradient = (lossPlus - lossMinus) / (2 * epsilon)
                    row.add(gradient)
                }
                plane.add(row)
            }
            numericalGradients.add(plane)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, calcDelta)[0] as IOType.D3

        // 数値微分と実際の勾配を比較
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                for (k in 0 until 2) {
                    assertEquals(
                        expected = numericalGradients[i][j][k],
                        actual = dx[i, j, k],
                        absoluteTolerance = 1e-3,
                        message = "要素[$i, $j, $k]の勾配が数値微分と一致しません",
                    )
                }
            }
        }
    }

    /**
     * 損失関数（テスト用）
     * loss = Σ(output_ijk * delta_ijk)
     */
    private fun calcLoss(output: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): Double {
        val delta = calcDelta(output)[0] as IOType.D3
        val out = output[0] as IOType.D3
        var loss = 0.0
        for (i in 0 until out.shape[0]) {
            for (j in 0 until out.shape[1]) {
                for (k in 0 until out.shape[2]) {
                    loss += out[i, j, k] * delta[i, j, k]
                }
            }
        }
        return loss
    }
}
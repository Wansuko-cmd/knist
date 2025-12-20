@file:Suppress("UNCHECKED_CAST")

package com.wsr.process.compute.norm.layer.d3

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.batch.toBatch
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.process.Context
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD3Test {
    @Test
    fun `LayerNormD3の_expect=Layer正規化を適用`() {
        // weight = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        val weight = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                e = 1e-10f,
            )

        // 2つのバッチ: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], [[[2, 4], [4, 6]], [[6, 8], [8, 10]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2 + 2).toFloat() },
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D3>
        // バッチ1: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], mean=4, numerator=[[[-4, -2], [-2, 0]], [[0, 2], [2, 4]]]
        // variance = (16 + 4 + 4 + 0 + 0 + 4 + 4 + 16) / 8 = 48 / 8 = 6, std=sqrt(6+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0]
        val expectedStd1 = sqrt(6.0f + 1e-10f)
        assertEquals(
            expected = (-4.0f / expectedStd1),
            actual = output1[0, 0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (-2.0f / expectedStd1),
            actual = output1[0, 0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (-2.0f / expectedStd1),
            actual = output1[0, 1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd1),
            actual = output1[0, 1, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd1),
            actual = output1[1, 0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd1),
            actual = output1[1, 0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd1),
            actual = output1[1, 1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (4.0f / expectedStd1),
            actual = output1[1, 1, 1],
            absoluteTolerance = 1e-4f,
        )

        // バッチ2: [[[2, 4], [4, 6]], [[6, 8], [8, 10]]], mean=6, numerator=[[[-4, -2], [-2, 0]], [[0, 2], [2, 4]]]
        // variance = (16 + 4 + 4 + 0 + 0 + 4 + 4 + 16) / 8 = 48 / 8 = 6, std=sqrt(6+1e-10)
        val output2 = result[1]
        val expectedStd2 = sqrt(6.0f + 1e-10f)
        assertEquals(
            expected = (-4.0f / expectedStd2),
            actual = output2[0, 0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (-2.0f / expectedStd2),
            actual = output2[0, 0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (-2.0f / expectedStd2),
            actual = output2[0, 1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd2),
            actual = output2[0, 1, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (0.0f / expectedStd2),
            actual = output2[1, 0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd2),
            actual = output2[1, 0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f / expectedStd2),
            actual = output2[1, 1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (4.0f / expectedStd2),
            actual = output2[1, 1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD3の_train=weightが更新される`() {
        // weight = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]]
        val weight = IOType.d3(2, 2, 2) { _, _, _ -> 2.0f }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                e = 1e-10f,
            )

        // 2つのバッチ: [[[0, 2], [2, 4]], [[4, 6], [6, 8]]], [[[2, 4], [4, 6]], [[6, 8], [8, 10]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z * 2 + 2).toFloat() },
            )
        val context = Context(input)

        // deltaは全て[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { outputs ->
            (0 until outputs.size).map { IOType.d3(2, 2, 2) { _, _, _ -> 1.0f } }.toBatch()
        }

        val expectedStd = sqrt(6.0f + 1e-10f)
        // normalized_avg ≈ [[[-1.633f, -0.816f], [-0.816f, 0]], [[0, 0.816f], [0.816f, 1.633f]]]
        // delta_avg = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        // dw = normalized_avg * delta_avg
        // weight -= 0.1f * dw

        // trainを実行
        norm._train(input, context, calcDelta) as Batch<IOType.D3>
        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(batchOf(input[0]), context) as Batch<IOType.D3>

        // output = updated_weight * normalized
        // normalized[0, 0, 0] = -4.0f / expectedStd
        // updated_weight[0, 0, 0] = 2.0f - 0.1f * (-4.0f / expectedStd) = 2.0f + 0.4f / expectedStd
        assertEquals(
            expected = (2.0f + 0.1f * 4.0f / expectedStd) * (-4.0f / expectedStd),
            actual = afterOutput[0][0, 0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f + 0.1f * 2.0f / expectedStd) * (-2.0f / expectedStd),
            actual = afterOutput[0][0, 0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (2.0f - 0.1f * 4.0f / expectedStd) * (4.0f / expectedStd),
            actual = afterOutput[0][1, 1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `LayerNormD3の数値微分テスト=dxが計算されて返される`() {
        // weight = [[[1.5f, 2.0f], [1.0f, 0.8f]], [[1.2f, 0.9f], [1.1f, 1.3f]]]
        val weight =
            IOType.d3(2, 2, 2) { x, y, z ->
                when {
                    x == 0 && y == 0 && z == 0 -> 1.5f
                    x == 0 && y == 0 && z == 1 -> 2.0f
                    x == 0 && y == 1 && z == 0 -> 1.0f
                    x == 0 && y == 1 && z == 1 -> 0.8f
                    x == 1 && y == 0 && z == 0 -> 1.2f
                    x == 1 && y == 0 && z == 1 -> 0.9f
                    x == 1 && y == 1 && z == 0 -> 1.1f
                    else -> 1.3f
                }
            }
        val norm =
            LayerNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                e = 1e-10f,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[[1, 0.5f], [-1, 0.8f]], [[0.7f, -0.5f], [0.9f, 1.2f]]]を返す（任意の勾配）
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    when {
                        x == 0 && y == 0 && z == 0 -> 1.0f
                        x == 0 && y == 0 && z == 1 -> 0.5f
                        x == 0 && y == 1 && z == 0 -> -1.0f
                        x == 0 && y == 1 && z == 1 -> 0.8f
                        x == 1 && y == 0 && z == 0 -> 0.7f
                        x == 1 && y == 0 && z == 1 -> -0.5f
                        x == 1 && y == 1 && z == 0 -> 0.9f
                        else -> 1.2f
                    }
                },
            )
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5f
        val numericalGradients = mutableListOf<List<List<Float>>>()

        for (i in 0 until 2) {
            val plane = mutableListOf<List<Float>>()
            for (j in 0 until 2) {
                val row = mutableListOf<Float>()
                for (k in 0 until 2) {
                    // input[i, j, k]を少し増やす
                    val inputPlus = input[0].value
                    inputPlus[i * 4 + j * 2 + k] += epsilon
                    val outputPlus = norm._expect(
                        batchOf(IOType.d3(listOf(2, 2, 2), inputPlus.toFloatArray())),
                        context,
                    )
                    val lossPlus = calcLoss(outputPlus, calcDelta)

                    // input[i, j, k]を少し減らす
                    val inputMinus = input[0].value
                    inputMinus[i * 4 + j * 2 + k] -= epsilon
                    val outputMinus = norm._expect(
                        batchOf(IOType.d3(listOf(2, 2, 2), inputMinus.toFloatArray())),
                        context,
                    )
                    val lossMinus = calcLoss(outputMinus, calcDelta)

                    // 数値微分
                    val gradient = ((lossPlus - lossMinus) / (2 * epsilon))
                    row.add(gradient)
                }
                plane.add(row)
            }
            numericalGradients.add(plane)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, context, calcDelta) as Batch<IOType.D3>

        // 数値微分と実際の勾配を比較
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                for (k in 0 until 2) {
                    assertEquals(
                        expected = numericalGradients[i][j][k],
                        actual = dx[0][i, j, k],
                        absoluteTolerance = 9e-2f,
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
    private fun calcLoss(output: Batch<IOType>, calcDelta: (Batch<IOType>) -> Batch<IOType>): Float {
        val delta = calcDelta(output) as Batch<IOType.D3>
        val out = output as Batch<IOType.D3>
        var loss = 0.0f
        for (i in 0 until out.shape[0]) {
            for (j in 0 until out.shape[1]) {
                for (k in 0 until out.shape[2]) {
                    loss += out[0][i, j, k] * delta[0][i, j, k]
                }
            }
        }
        return loss
    }
}

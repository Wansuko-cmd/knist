package com.wsr.layer.process.norm.layer.d1

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerNormD1Test {
    @Test
    fun `LayerNormD1の_expect=Layer正規化を適用`() {
        // weight = [1, 1]
        val weight = IOType.Companion.d1(listOf(1.0, 1.0))
        val norm =
            LayerNormD1(
                outputSize = 2,
                optimizer = Sgd(0.1).d1(size = weight.shape[0]),
                weight = weight,
            )

        // 2つのバッチ: [2, 4], [1, 3]
        val input =
            listOf(
                IOType.Companion.d1(listOf(2.0, 4.0)),
                IOType.Companion.d1(listOf(1.0, 3.0)),
            )

        val result = norm._expect(input)

        // バッチ1: mean=3, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // output = [-1, 1] / sqrt(1+1e-10)
        assertEquals(expected = 2, actual = result.size)
        val output1 = result[0] as IOType.D1
        val expectedStd1 = sqrt(1.0 + 1e-10)
        assertEquals(expected = -1.0 / expectedStd1, actual = output1[0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 / expectedStd1, actual = output1[1], absoluteTolerance = 1e-4)

        // バッチ2: mean=2, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // output = [-1, 1] / sqrt(1+1e-10)
        val output2 = result[1] as IOType.D1
        val expectedStd2 = sqrt(1.0 + 1e-10)
        assertEquals(expected = -1.0 / expectedStd2, actual = output2[0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 / expectedStd2, actual = output2[1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `LayerNormD1の_train=weightが更新される`() {
        // weight = [2, 2]
        val weight = IOType.Companion.d1(listOf(2.0, 2.0))
        val norm =
            LayerNormD1(
                outputSize = 2,
                optimizer = Sgd(0.1).d1(size = weight.shape[0]),
                weight = weight,
            )

        // 2つのバッチ: [0, 2], [2, 4]
        val input =
            listOf(
                IOType.Companion.d1(listOf(0.0, 2.0)),
                IOType.Companion.d1(listOf(2.0, 4.0)),
            )

        // deltaは全て[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = { outputs ->
            outputs.map { IOType.Companion.d1(listOf(1.0, 1.0)) }
        }

        // バッチ1: mean=1, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // normalized1 = [-1, 1] / sqrt(1+1e-10) ≈ [-1, 1]
        // バッチ2: mean=3, numerator=[-1, 1], variance=1, std=sqrt(1+1e-10)
        // normalized2 = [-1, 1] / sqrt(1+1e-10) ≈ [-1, 1]
        // normalized_avg = ([-1, 1] + [-1, 1]) / 2 = [-1, 1]
        // delta_avg = ([1, 1] + [1, 1]) / 2 = [1, 1]
        // dw = normalized_avg * delta_avg = [-1, 1] * [1, 1] = [-1, 1]
        // weight -= 0.1 * dw = [2, 2] - 0.1 * [-1, 1] = [2, 2] - [-0.1, 0.1] = [2.1, 1.9]

        // trainを実行
        try {
            norm._train(input, calcDelta)
        } catch (e: NotImplementedError) {
            // dxの計算が未実装なので例外をキャッチ
        }

        // 更新後のexpect結果を確認（バッチ1で）
        val afterOutput = norm._expect(listOf(input[0]))[0] as IOType.D1
        val expectedStd = sqrt(1.0 + 1e-10)
        // output = [2.1, 1.9] * [-1, 1] / std
        assertEquals(
            expected = 2.1 * (-1.0 / expectedStd),
            actual = afterOutput[0],
            absoluteTolerance = 1e-4,
        )
        assertEquals(
            expected = 1.9 * (1.0 / expectedStd),
            actual = afterOutput[1],
            absoluteTolerance = 1e-4,
        )
    }

    @Test
    fun `LayerNormD1の数値微分テスト=dxが計算されて返される`() {
        // weight = [1.5, 2.0, 1.0]
        val weight = IOType.Companion.d1(listOf(1.5, 2.0, 1.0))
        val norm =
            LayerNormD1(
                outputSize = 3,
                optimizer = Sgd(0.01).d1(size = weight.shape[0]),
                weight = weight,
            )

        // [1, 4, 7]
        val input =
            listOf(
                IOType.Companion.d1(listOf(1.0, 4.0, 7.0)),
            )

        // deltaは[1, 0.5, -1]を返す（任意の勾配）
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.Companion.d1(listOf(1.0, 0.5, -1.0)))
        }

        // 数値微分でdxを計算
        val epsilon = 1e-5
        val numericalGradients = mutableListOf<Double>()

        for (i in 0 until 3) {
            // input[i]を少し増やす
            val inputPlus = input[0].value.toMutableList()
            inputPlus[i] += epsilon
            val outputPlus = norm._expect(listOf(IOType.Companion.d1(inputPlus)))
            val lossPlus = calcLoss(outputPlus, calcDelta)

            // input[i]を少し減らす
            val inputMinus = input[0].value.toMutableList()
            inputMinus[i] -= epsilon
            val outputMinus = norm._expect(listOf(IOType.Companion.d1(inputMinus)))
            val lossMinus = calcLoss(outputMinus, calcDelta)

            // 数値微分
            val gradient = (lossPlus - lossMinus) / (2 * epsilon)
            numericalGradients.add(gradient)
        }

        // 実際のdxを計算（trainメソッドから）
        val dx = norm._train(input, calcDelta)[0] as IOType.D1

        // 数値微分と実際の勾配を比較
        for (i in 0 until 3) {
            assertEquals(
                expected = numericalGradients[i],
                actual = dx[i],
                absoluteTolerance = 1e-3,
                message = "要素[$i]の勾配が数値微分と一致しません",
            )
        }
    }

    /**
     * 損失関数（テスト用）
     * loss = Σ(output_i * delta_i)
     */
    private fun calcLoss(output: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): Double {
        val delta = calcDelta(output)[0] as IOType.D1
        val out = output[0] as IOType.D1
        var loss = 0.0
        for (i in 0 until out.value.size) {
            loss += out[i] * delta[i]
        }
        return loss
    }
}

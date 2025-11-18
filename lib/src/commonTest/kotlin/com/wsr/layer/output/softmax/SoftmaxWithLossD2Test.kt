@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.output.softmax.SoftmaxWithLossD2
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossD2Test {
    @Test
    fun `SoftmaxWithLossD2の_expect=各行にsoftmaxを適用した値を返す`() {
        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        val softmax = SoftmaxWithLossD2(outputX = 2, outputY = 3, temperature = 1.0f)
        val result = softmax._expect(input)

        // 行0: [1, 2, 3]のsoftmax
        // max = 3
        val exp00 = exp(1.0f - 3.0f)
        val exp01 = exp(2.0f - 3.0f)
        val exp02 = exp(3.0f - 3.0f)
        val sum0 = exp00 + exp01 + exp02

        // 行1: [4, 5, 6]のsoftmax
        // max = 6
        val exp10 = exp(4.0f - 6.0f)
        val exp11 = exp(5.0f - 6.0f)
        val exp12 = exp(6.0f - 6.0f)
        val sum1 = exp10 + exp11 + exp12

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 3, actual = output.shape[1])

        // 行0の出力確認
        assertEquals(expected = exp00 / sum0, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp01 / sum0, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp02 / sum0, actual = output[0, 2], absoluteTolerance = 1e-4f)

        // 行1の出力確認
        assertEquals(expected = exp10 / sum1, actual = output[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp11 / sum1, actual = output[1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp12 / sum1, actual = output[1, 2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD2の_train=各行にsoftmax適用後にラベルを引いた値を返す`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )
        // [[0, 0, 1]] (one-hot: クラス2が正解)
        val label =
            listOf(
                IOType.d2(1, 3) { _, y -> if (y == 2) 1.0f else 0.0f },
            )
        val softmax = SoftmaxWithLossD2(outputX = 1, outputY = 3, temperature = 1.0f)
        val result = softmax._train(input, label)

        // 行0に対してsoftmax:
        // max = 3
        // exp(1-3) = exp(-2)
        // exp(2-3) = exp(-1)
        // exp(3-3) = exp(0) = 1
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        // [[exp0/sum - 0, exp1/sum - 0, exp2/sum - 1]]
        val output = result[0] as IOType.D2
        assertEquals(expected = 1, actual = output.shape[0])
        assertEquals(expected = 3, actual = output.shape[1])
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1f),
            actual = output[0, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=複数行それぞれに独立してsoftmaxを適用`() {
        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        // [[1, 0], [0, 1]] (行0はクラス0、行1はクラス1が正解)
        val label =
            listOf(
                IOType.d2(2, 2) { x, y -> if (x == y) 1.0f else 0.0f },
            )
        val softmax = SoftmaxWithLossD2(outputX = 2, outputY = 2, temperature = 1.0f)
        val result = softmax._train(input, label)

        // 行0: softmax([1, 2])
        val max0 = 2.0f
        val exp00 = exp(1.0f - max0)
        val exp01 = exp(2.0f - max0)
        val sum0 = exp00 + exp01

        // 行1: softmax([3, 4])
        val max1 = 4.0f
        val exp10 = exp(3.0f - max1)
        val exp11 = exp(4.0f - max1)
        val sum1 = exp10 + exp11

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // 行0: [exp00/sum0 - 1, exp01/sum0 - 0]
        assertEquals(
            expected = (exp00 / sum0 - 1f),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp01 / sum0 - 0f),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )

        // 行1: [exp10/sum1 - 0, exp11/sum1 - 1]
        assertEquals(
            expected = (exp10 / sum1 - 0f),
            actual = output[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp11 / sum1 - 1f),
            actual = output[1, 1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=複数バッチでも正しく動作する`() {
        // バッチ1: [[1, 2, 3]]
        // バッチ2: [[4, 5, 6]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
                IOType.d2(1, 3) { _, y -> (y + 4).toFloat() },
            )
        // バッチ1のラベル: [[1, 0, 0]] (クラス0が正解)
        // バッチ2のラベル: [[0, 0, 1]] (クラス2が正解)
        val label =
            listOf(
                IOType.d2(1, 3) { _, y -> if (y == 0) 1.0f else 0.0f },
                IOType.d2(1, 3) { _, y -> if (y == 2) 1.0f else 0.0f },
            )
        val softmax = SoftmaxWithLossD2(outputX = 1, outputY = 3, temperature = 1.0f)
        val result = softmax._train(input, label)

        // バッチ1: softmax([1, 2, 3])
        val max1 = 3.0f
        val exp10 = exp(1.0f - max1)
        val exp11 = exp(2.0f - max1)
        val exp12 = exp(3.0f - max1)
        val sum1 = exp10 + exp11 + exp12

        // バッチ2: softmax([4, 5, 6])
        val max2 = 6.0f
        val exp20 = exp(4.0f - max2)
        val exp21 = exp(5.0f - max2)
        val exp22 = exp(6.0f - max2)
        val sum2 = exp20 + exp21 + exp22

        assertEquals(expected = 2, actual = result.size)

        // バッチ1の出力: [[exp10/sum1 - 1, exp11/sum1 - 0, exp12/sum1 - 0]]
        val output1 = result[0] as IOType.D2
        assertEquals(
            expected = (exp10 / sum1 - 1f),
            actual = output1[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp11 / sum1 - 0f),
            actual = output1[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp12 / sum1 - 0f),
            actual = output1[0, 2],
            absoluteTolerance = 1e-4f,
        )

        // バッチ2の出力: [[exp20/sum2 - 0, exp21/sum2 - 0, exp22/sum2 - 1]]
        val output2 = result[1] as IOType.D2
        assertEquals(
            expected = (exp20 / sum2 - 0.0f),
            actual = output2[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp21 / sum2 - 0.0f),
            actual = output2[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp22 / sum2 - 1.0f),
            actual = output2[0, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=temperature適用後にsoftmaxを計算`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )
        // [[0, 0, 1]]
        val label =
            listOf(
                IOType.d2(1, 3) { _, y -> if (y == 2) 1.0f else 0.0f },
            )
        // temperature = 2.0で分布を平滑化
        val softmax = SoftmaxWithLossD2(outputX = 1, outputY = 3, temperature = 2.0f)
        val result = softmax._train(input, label)

        // temperature適用: [1/2, 2/2, 3/2] = [0.5f, 1.0f, 1.5f]
        // max = 1.5f
        // exp(0.5f-1.5f) = exp(-1.0f)
        // exp(1.0f-1.5f) = exp(-0.5f)
        // exp(1.5f-1.5f) = exp(0) = 1
        val exp0 = exp(0.5f - 1.5f)
        val exp1 = exp(1.0f - 1.5f)
        val exp2 = exp(1.5f - 1.5f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(
            expected = (exp0 / sum - 0.0f),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0.0f),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1.0f),
            actual = output[0, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=数値安定性の確認_大きな値でもオーバーフローしない`() {
        // [[100, 200, 300]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> ((y + 1) * 100).toFloat() },
            )
        // [[0, 0, 1]]
        val label =
            listOf(
                IOType.d2(1, 3) { _, y -> if (y == 2) 1.0f else 0.0f },
            )
        val softmax = SoftmaxWithLossD2(outputX = 1, outputY = 3, temperature = 1.0f)
        val result = softmax._train(input, label)

        // maxを引くことで数値安定性を確保
        // max = 300
        // exp(100-300) = exp(-200) ≈ 0
        // exp(200-300) = exp(-100) ≈ 0
        // exp(300-300) = exp(0) = 1
        // softmax ≈ [0, 0, 1]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // ほぼ[0-0, 0-0, 1-1] = [0, 0, 0]になる
        assertEquals(expected = 0.0f, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[0, 2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD2の_train=maskValueで指定したインデックスが1_0の要素は勾配が0になる`() {
        // [[1, 2, 3, 0], [4, 5, 6, 0]]
        val input =
            listOf(
                IOType.d2(2, 4) { x, y ->
                    // クラス数は3、インデックス3はパディングフラグ
                    if (y < 3) (x * 3 + y + 1).toFloat() else 0.0f
                },
            )
        // [[0, 0, 1, 0], [0, 0, 0, 1]]  ← 行0はクラス2、行1はパディング(index=3が1.0)
        val label =
            listOf(
                IOType.d2(2, 4) { x, y ->
                    when {
                        x == 0 && y == 2 -> 1.0f // 行0: クラス2が正解
                        x == 1 && y == 3 -> 1.0f // 行1: パディングフラグ
                        else -> 0.0f
                    }
                },
            )
        // maskValue=3: インデックス3の値が1.0ならパディング
        val softmax = SoftmaxWithLossD2(outputX = 2, outputY = 4, temperature = 1.0f, maskValue = 3)
        val result = softmax._train(input, label)

        // 行0のsoftmax計算(4要素全てで計算される)
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val exp3 = exp(0.0f - 3.0f)
        val sum = exp0 + exp1 + exp2 + exp3

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // 行0: 通常の勾配(パディングでない)
        assertEquals(
            expected = (exp0 / sum - 0.0f),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0.0f),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1.0f),
            actual = output[0, 2],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp3 / sum - 0.0f),
            actual = output[0, 3],
            absoluteTolerance = 1e-4f,
        )

        // 行1: maskValue=3のインデックスが1.0なので全要素の勾配が0
        assertEquals(expected = 0.0f, actual = output[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[1, 2], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[1, 3], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD2の_train=maskValue=nullの場合は全要素が有効`() {
        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        // [[-1, -1, 1], [0, 0, 1]]  ← -1を含むがmaskValue=nullなので無視されない
        val label =
            listOf(
                IOType.d2(2, 3) { x, y ->
                    when {
                        x == 0 && y < 2 -> -1.0f
                        y == 2 -> 1.0f
                        else -> 0.0f
                    }
                },
            )
        val softmax =
            SoftmaxWithLossD2(outputX = 2, outputY = 3, temperature = 1.0f, maskValue = null)
        val result = softmax._train(input, label)

        // 行0のsoftmax
        val exp00 = exp(1.0f - 3.0f)
        val exp01 = exp(2.0f - 3.0f)
        val exp02 = exp(3.0f - 3.0f)
        val sum0 = exp00 + exp01 + exp02

        // 行1のsoftmax
        val exp10 = exp(4.0f - 6.0f)
        val exp11 = exp(5.0f - 6.0f)
        val exp12 = exp(6.0f - 6.0f)
        val sum1 = exp10 + exp11 + exp12

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // 全要素が有効なので通常の勾配（-1.0もラベル値として扱われる）
        assertEquals(
            expected = (exp00 / sum0 - (-1.0f)),
            actual = output[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp01 / sum0 - (-1.0f)),
            actual = output[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp02 / sum0 - 1.0f),
            actual = output[0, 2],
            absoluteTolerance = 1e-4f,
        )

        assertEquals(
            expected = (exp10 / sum1 - 0.0f),
            actual = output[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp11 / sum1 - 0.0f),
            actual = output[1, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp12 / sum1 - 1.0f),
            actual = output[1, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=複数バッチでmaskValueが正しく適用される`() {
        // バッチ1: [[1, 2, 3, 0], [4, 5, 6, 0]]  ← インデックス3はパディングフラグ
        // バッチ2: [[7, 8, 9, 0], [10, 11, 12, 0]]
        val input =
            listOf(
                IOType.d2(2, 4) { x, y ->
                    if (y < 3) (x * 3 + y + 1).toFloat() else 0.0f
                },
                IOType.d2(2, 4) { x, y ->
                    if (y < 3) (x * 3 + y + 7).toFloat() else 0.0f
                },
            )
        // バッチ1: [[0, 0, 1, 0], [0, 0, 0, 1]]  ← 行1はパディング(index=3が1.0)
        // バッチ2: [[1, 0, 0, 0], [0, 1, 0, 0]]  ← マスクなし
        val label =
            listOf(
                IOType.d2(2, 4) { x, y ->
                    when {
                        x == 0 && y == 2 -> 1.0f // 行0: クラス2が正解
                        x == 1 && y == 3 -> 1.0f // 行1: パディングフラグ
                        else -> 0.0f
                    }
                },
                IOType.d2(2, 4) { x, y ->
                    when {
                        x == 0 && y == 0 -> 1.0f
                        x == 1 && y == 1 -> 1.0f
                        else -> 0.0f
                    }
                },
            )
        val softmax = SoftmaxWithLossD2(outputX = 2, outputY = 4, temperature = 1.0f, maskValue = 3)
        val result = softmax._train(input, label)

        assertEquals(expected = 2, actual = result.size)

        // バッチ1
        val output1 = result[0] as IOType.D2
        // 行0: 通常の勾配あり（パディングでない）
        // 値は0でないことを確認
        // 行1: マスクされているので全て0
        assertEquals(expected = 0.0f, actual = output1[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output1[1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output1[1, 2], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output1[1, 3], absoluteTolerance = 1e-4f)

        // バッチ2: マスクなし、全て通常の勾配
        val output2 = result[1] as IOType.D2
        // バッチ2の行0, 行1のsoftmax計算
        val exp20 = exp(7.0f - 9.0f)
        val exp21 = exp(8.0f - 9.0f)
        val exp22 = exp(9.0f - 9.0f)
        val sum20 = exp20 + exp21 + exp22

        val exp30 = exp(10.0f - 12.0f)
        val exp31 = exp(11.0f - 12.0f)
        val exp32 = exp(12.0f - 12.0f)
        val sum21 = exp30 + exp31 + exp32

        // 行0はクラス0が正解なので、勾配は [softmax - 1, softmax, softmax]
        assertEquals(
            expected = (exp20 / sum20 - 1.0f),
            actual = output2[0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp21 / sum20 - 0.0f),
            actual = output2[0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp22 / sum20 - 0.0f),
            actual = output2[0, 2],
            absoluteTolerance = 1e-4f,
        )

        // 行1はクラス1が正解なので、勾配は [softmax, softmax - 1, softmax]
        assertEquals(
            expected = (exp30 / sum21 - 0.0f),
            actual = output2[1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp31 / sum21 - 1.0f),
            actual = output2[1, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp32 / sum21 - 0.0f),
            actual = output2[1, 2],
            absoluteTolerance = 1e-4f,
        )
    }
}

@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.output.softmax

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.output.softmax.SoftmaxWithLossD2
import com.wsr.core.set
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class SoftmaxWithLossD2Test {
    @Test
    fun `SoftmaxWithLossD2の_expect=各行にsoftmaxを適用した値を返す`() {
        // [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
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
        val output = result as Batch<IOType.D2>
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 3, actual = output.shape[1])

        // 行0の出力確認
        assertEquals(expected = exp00 / sum0, actual = output[0][0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp01 / sum0, actual = output[0][0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp02 / sum0, actual = output[0][0, 2], absoluteTolerance = 1e-4f)

        // 行1の出力確認
        assertEquals(expected = exp10 / sum1, actual = output[0][1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp11 / sum1, actual = output[0][1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = exp12 / sum1, actual = output[0][1, 2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD2の_train=各行にsoftmax適用後にラベルを引いた値を返す`() {
        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )
        // [[0, 0, 1]] (one-hot: クラス2が正解)
        val label =
            batchOf(
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

        // loss = mean(flatMap(-ln(sum_axis1(label * softmax) + 1e-7)))
        // sum(axis=1) sums across columns for each row
        // label = [[0, 0, 1]], softmax = [[exp0/sum, exp1/sum, exp2/sum]]
        // sum_axis1(label * softmax) = [0*(exp0/sum) + 0*(exp1/sum) + 1*(exp2/sum)] = [exp2/sum]
        // flatMap(-ln([exp2/sum] + ε)) = [-ln(exp2/sum + ε)]
        // loss = average([-ln(exp2/sum + ε)]) = -ln(exp2/sum + ε)
        val epsilon = 1e-7f
        val softmax0 = exp0 / sum
        val softmax1 = exp1 / sum
        val softmax2 = exp2 / sum
        val row0Sum = 0.0f * softmax0 + 0.0f * softmax1 + 1.0f * softmax2
        val expectedLoss = -kotlin.math.ln(row0Sum + epsilon)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        // [[exp0/sum - 0, exp1/sum - 0, exp2/sum - 1]]
        val output = result.delta as Batch<IOType.D2>
        assertEquals(expected = 1, actual = output.shape[0])
        assertEquals(expected = 3, actual = output.shape[1])
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1f),
            actual = output[0][0, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=temperature適用後にsoftmaxを計算`() {
        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )
        // [[0, 0, 1]]
        val label =
            batchOf(
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

        // loss = mean(flatMap(-ln(sum_axis1(label * softmax) + 1e-7)))
        // temperature=2.0適用後にsoftmax計算
        // label = [[0, 0, 1]], softmax = [[exp0/sum, exp1/sum, exp2/sum]]
        // sum_axis1(label * softmax) = [0*(exp0/sum) + 0*(exp1/sum) + 1*(exp2/sum)] = [exp2/sum]
        // loss = -ln(exp2/sum + ε)
        val epsilon = 1e-7f
        val softmax0 = exp0 / sum
        val softmax1 = exp1 / sum
        val softmax2 = exp2 / sum
        val row0Sum = 0.0f * softmax0 + 0.0f * softmax1 + 1.0f * softmax2
        val expectedLoss = -kotlin.math.ln(row0Sum + epsilon)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta as Batch<IOType.D2>
        assertEquals(
            expected = (exp0 / sum - 0.0f),
            actual = output[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0.0f),
            actual = output[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1.0f),
            actual = output[0][0, 2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD2の_train=maskValueで指定したインデックスが1_0の要素は勾配が0になる`() {
        // [[1, 2, 3, 0], [4, 5, 6, 0]]
        val input =
            batchOf(
                IOType.d2(2, 4) { x, y ->
                    // クラス数は3、インデックス3はパディングフラグ
                    if (y < 3) (x * 3 + y + 1).toFloat() else 0.0f
                },
            )
        // [[0, 0, 1, 0], [0, 0, 0, 1]]  ← 行0はクラス2、行1はパディング(index=3が1.0)
        val label =
            batchOf(
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

        // 行0のsoftmax計算: [1, 2, 3, 0]
        val exp00 = exp(1.0f - 3.0f)
        val exp01 = exp(2.0f - 3.0f)
        val exp02 = exp(3.0f - 3.0f)
        val exp03 = exp(0.0f - 3.0f)
        val sum0 = exp00 + exp01 + exp02 + exp03

        // 行1のsoftmax計算: [4, 5, 6, 0]
        val exp10 = exp(4.0f - 6.0f)
        val exp11 = exp(5.0f - 6.0f)
        val exp12 = exp(6.0f - 6.0f)
        val exp13 = exp(0.0f - 6.0f)
        val sum1 = exp10 + exp11 + exp12 + exp13

        // loss = mean(flatMap(-ln(sum_axis1(label * softmax) + 1e-7)))
        // label = [[0, 0, 1, 0], [0, 0, 0, 1]]
        // 行0: クラス2が正解、sum_axis1 = 0*(exp00/sum0) + 0*(exp01/sum0) + 1*(exp02/sum0) + 0*(exp03/sum0) = exp02/sum0
        // 行1: maskValue=3のインデックスが1.0なのでパディング行、sum_axis1 = 0+0+0+1*(exp13/sum1) = exp13/sum1
        // 両行のlossが計算され平均される
        // loss = average([-ln(exp02/sum0 + ε), -ln(exp13/sum1 + ε)])
        val epsilon = 1e-7f
        val softmax00 = exp00 / sum0
        val softmax01 = exp01 / sum0
        val softmax02 = exp02 / sum0
        val softmax03 = exp03 / sum0
        val softmax10 = exp10 / sum1
        val softmax11 = exp11 / sum1
        val softmax12 = exp12 / sum1
        val softmax13 = exp13 / sum1
        val row0Sum = 0.0f * softmax00 + 0.0f * softmax01 + 1.0f * softmax02 + 0.0f * softmax03
        val row1Sum = 0.0f * softmax10 + 0.0f * softmax11 + 0.0f * softmax12 + 1.0f * softmax13
        val expectedLoss = (-kotlin.math.ln(row0Sum + epsilon) + (-kotlin.math.ln(row1Sum + epsilon))) / 2.0f
//        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta as Batch<IOType.D2>

        // 行0: 通常の勾配(パディングでない)
        assertEquals(
            expected = (exp00 / sum0 - 0.0f),
            actual = output[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp01 / sum0 - 0.0f),
            actual = output[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp02 / sum0 - 1.0f),
            actual = output[0][0, 2],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp03 / sum0 - 0.0f),
            actual = output[0][0, 3],
            absoluteTolerance = 1e-4f,
        )

        // 行1: maskValue=3のインデックスが1.0なので全要素の勾配が0
        assertEquals(expected = 0.0f, actual = output[0][1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[0][1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[0][1, 2], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0f, actual = output[0][1, 3], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD2の_train=maskValue=nullの場合は全要素が有効`() {
        // [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        // [[-1, -1, 1], [0, 0, 1]]  ← -1を含むがmaskValue=nullなので無視されない
        val label =
            batchOf(
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

        // loss = mean(flatMap(-ln(sum_axis1(label * softmax) + 1e-7)))
        // label = [[-1, -1, 1], [0, 0, 1]], softmax = [[exp00/sum0, exp01/sum0, exp02/sum0], [exp10/sum1, exp11/sum1, exp12/sum1]]
        // maskValue=nullなので全要素が有効、-1もラベル値として扱われる
        // 行0: sum_axis1 = (-1)*(exp00/sum0) + (-1)*(exp01/sum0) + 1*(exp02/sum0)
        // 行1: sum_axis1 = 0*(exp10/sum1) + 0*(exp11/sum1) + 1*(exp12/sum1) = exp12/sum1
        // loss = average([-ln(row0_sum + ε), -ln(row1_sum + ε)])
        val epsilon = 1e-7f
        val softmax00 = exp00 / sum0
        val softmax01 = exp01 / sum0
        val softmax02 = exp02 / sum0
        val softmax10 = exp10 / sum1
        val softmax11 = exp11 / sum1
        val softmax12 = exp12 / sum1
        val row0Sum = (-1.0f) * softmax00 + (-1.0f) * softmax01 + 1.0f * softmax02
        val row1Sum = 0.0f * softmax10 + 0.0f * softmax11 + 1.0f * softmax12
        val expectedLoss = (-kotlin.math.ln(row0Sum + epsilon) + (-kotlin.math.ln(row1Sum + epsilon))) / 2.0f
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta as Batch<IOType.D2>

        // 全要素が有効なので通常の勾配（-1.0もラベル値として扱われる）
        assertEquals(
            expected = (exp00 / sum0 - (-1.0f)),
            actual = output[0][0, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp01 / sum0 - (-1.0f)),
            actual = output[0][0, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp02 / sum0 - 1.0f),
            actual = output[0][0, 2],
            absoluteTolerance = 1e-4f,
        )

        assertEquals(
            expected = (exp10 / sum1 - 0.0f),
            actual = output[0][1, 0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp11 / sum1 - 0.0f),
            actual = output[0][1, 1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp12 / sum1 - 1.0f),
            actual = output[0][1, 2],
            absoluteTolerance = 1e-4f,
        )
    }
}

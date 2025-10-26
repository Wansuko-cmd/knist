package com.wsr.layer.process.attention

import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.collection.sum
import com.wsr.dot.matmul.matMul
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.toD2
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.sqrt

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private var weightQ: IOType.D3,
    private var weightK: IOType.D3,
    private var weightV: IOType.D3,
    private val optimizerQ: Optimizer.D3,
    private val optimizerK: Optimizer.D3,
    private val optimizerV: Optimizer.D3,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val query = affine(input, weightQ)
        val key = affine(input, weightK)
        val value = affine(input, weightV)

        val mul = query.matMul(key.transpose())
        val scaled = mul / sqrt(outputY.toDouble())

        val mask = IOType.d2(outputX, outputY) { x, y -> if (x > y) -1e9 else 0.0 }
        val masked = scaled + mask

        val softmax = softmax(masked)
        return softmax.matMul(value)
    }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val query = affine(input, weightQ)
        val key = affine(input, weightK)
        val value = affine(input, weightV)

        val mul = query.matMul(key.transpose())
        val scaled = mul / sqrt(outputY.toDouble())

        val mask = IOType.d2(outputX, outputY) { x, y -> if (x > y) -1e9 else 0.0 }
        val masked = scaled + mask

        val softmax = softmax(masked)
        val output = softmax.matMul(value)

        val delta = calcDelta(output)

        // scaled dot attentionの逆伝播

        // dwq, dwk, dwv, dxの計算

        // wq, wk, wvの更新式

        // dxを返す
        TODO()
    }

    private fun affine(input: List<IOType.D2>, weight: IOType.D3): List<IOType.D2> {
        val weight = (0 until outputX).map { weight[it].transpose() }
        return input.map { input ->
            (0 until outputX)
                .map { weight[it].matMul(input[it]) }
                .toD2()
        }
    }

    private fun softmax(input: List<IOType.D2>): List<IOType.D2> = input.map { input ->
        val max = input.max(axis = 1)
        val exp = IOType.d2(shape = input.shape) { x, y -> exp(input[x, y] - max[x]) }
        val sum = exp.sum(axis = 1)
        IOType.d2(input.shape) { x, y -> exp[x, y] / sum[x] }
    }
}

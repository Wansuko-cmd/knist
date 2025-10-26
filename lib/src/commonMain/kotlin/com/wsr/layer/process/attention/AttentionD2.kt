package com.wsr.layer.process.attention

import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.collection.sum
import com.wsr.dot.matmul.matMul
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.reshape.toD2
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.sqrt

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val weightQ: IOType.D3,
    private val weightK: IOType.D3,
    private val weightV: IOType.D3,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val query = affine(input, weightQ)
        val key = affine(input, weightK)
        val value = affine(input, weightV)
        return scaledDotAttention(query, key, value)
    }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
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

    private fun scaledDotAttention(
        query: List<IOType.D2>,
        key: List<IOType.D2>,
        value: List<IOType.D2>,
    ) = List(query.size) { index ->
        val query = query[index]
        val key = key[index]
        val value = value[index]

        val mul = query.matMul(key.transpose())
        val scaled = mul / sqrt(key.shape[1].toDouble())

        val mask = IOType.d2(scaled.shape) { x, y -> if (x > y) -1e9 else 0.0 }
        val masked = scaled + mask

        val softmax = softmax(masked)
        softmax.matMul(value)
    }

    private fun softmax(input: IOType.D2): IOType.D2 {
        val max = input.max(axis = 1)
        val exp = IOType.d2(shape = input.shape) { x, y -> exp(input[x, y] - max[x]) }
        val sum = exp.sum(axis = 1)
        return IOType.d2(input.shape) { x, y -> exp[x, y] / sum[x] }
    }
}

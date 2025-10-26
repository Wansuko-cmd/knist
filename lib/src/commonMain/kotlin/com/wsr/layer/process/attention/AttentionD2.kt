package com.wsr.layer.process.attention

import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.collection.sum
import com.wsr.dot.matmul.matMul
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.toD2
import com.wsr.reshape.toD3
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.sqrt

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val numOfHeads: Int,
    private var weightQ: List<IOType.D3>,
    private var weightK: List<IOType.D3>,
    private var weightV: List<IOType.D3>,
    private var weightO: IOType.D3,
    private val optimizerQ: List<Optimizer.D3>,
    private val optimizerK: List<Optimizer.D3>,
    private val optimizerV: List<Optimizer.D3>,
    private val optimizerO: Optimizer.D3,
) : Process.D2() {
    private val dk = outputY / numOfHeads

    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val heads = List(numOfHeads) {
            val query = affine(input, weightQ[it])
            val key = affine(input, weightK[it])
            val value = affine(input, weightV[it])

            val mul = query.matMul(key.transpose())
            val scaled = mul / sqrt(dk.toDouble())

            val mask = IOType.d2(outputX, outputX) { x, y -> if (x > y) -1e9 else 0.0 }
            val masked = scaled + mask

            val softmax = softmax(masked)
            softmax.matMul(value)
        }
        val concat = List(input.size) {
            IOType.d2(outputX, outputY) { x, y ->
                heads[y / dk][it][x, y % dk]
            }
        }
        return affine(concat, weightO)
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
        val dValue = softmax.transpose().matMul(delta)
        val dSoftmax = delta.matMul(value.transpose())

        val sum = (dSoftmax * softmax).sum(axis = 1)
        val dMasked = List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                softmax[i][x, y] * (dSoftmax[i][x, y] - sum[i][x])
            }
        }

        val dScaled = dMasked
        val dMul = dScaled / sqrt(outputY.toDouble())

        val dQuery = dMul.matMul(key)
        val dKey = dMul.transpose().matMul(query)

        // dwq, dwk, dwv, dxの計算
        val dwi = input.toD3().transpose(1, 2, 0)

        val dxq = dQuery.map { delta -> (0 until outputX).map { weightQ[it].matMul(delta[it]) }.toD2() }
        val dqw = dQuery.toD3().transpose(1, 0, 2)
        val dwq = (0 until outputX).map { dwi[it].matMul(dqw[it]) }.toD3() / input.size.toDouble()

        val dxk = dKey.map { delta -> (0 until outputX).map { weightK[it].matMul(delta[it]) }.toD2() }
        val dkw = dKey.toD3().transpose(1, 0, 2)
        val dwk = (0 until outputX).map { dwi[it].matMul(dkw[it]) }.toD3() / input.size.toDouble()

        val dxv = dValue.map { delta -> (0 until outputX).map { weightV[it].matMul(delta[it]) }.toD2() }
        val dvw = dValue.toD3().transpose(1, 0, 2)
        val dwv = (0 until outputX).map { dwi[it].matMul(dvw[it]) }.toD3() / input.size.toDouble()


        // wq, wk, wvの更新式
        weightQ = optimizerQ.adapt(weightQ, dwq)
        weightK = optimizerK.adapt(weightK, dwk)
        weightV = optimizerV.adapt(weightV, dwv)

        // dxを返す
        return dxq + dxk + dxv
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

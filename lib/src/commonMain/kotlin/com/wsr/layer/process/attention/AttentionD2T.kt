package com.wsr.layer.process.attention

import com.wsr.batch.Batch
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.get
import com.wsr.batch.math.softmax
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.matmul.matMul
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.batch.reshape.reshapeToD2
import com.wsr.batch.reshape.reshapeToD3
import com.wsr.batch.reshape.transpose
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.reshape.broadcastToD2
import com.wsr.core.reshape.transpose
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
class AttentionD2T internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val numOfHeads: Int,
    private val dim: Int,
    val maskValue: Int? = null,
    private var weightQ: IOType.D2,
    private var weightK: IOType.D2,
    private var weightV: IOType.D2,
    private val optimizerQ: Optimizer.D2,
    private val optimizerK: Optimizer.D2,
    private val optimizerV: Optimizer.D2,
    private var weightO: IOType.D2,
    private val optimizerO: Optimizer.D2,
) : Process.D2() {
    private val mask by lazy { IOType.d2(outputX, outputX) { x, y -> if (x < y) -1e9f else 0f } }
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val query = input.matMul(weightQ)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = scaled + mask + context.generatePaddingMask()
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = Batch(input.size) { batchIndex ->
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                val headIndex = y / dim
                val dimIndex = y % dim
                heads[batchIndex][headIndex, x, dimIndex]
            }
        }
        return concat.matMul(weightO)
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val query = input.matMul(weightQ)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = scaled + mask + context.generatePaddingMask()
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = Batch(input.size) { batchIndex ->
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                val headIndex = y / dim
                val dimIndex = y % dim
                heads[batchIndex][headIndex, x, dimIndex]
            }
        }

        val output = concat.matMul(weightO)
        val delta = calcDelta(output)

        // 出力変換（weightO）の逆伝播
        val dConcat = delta.matMul(weightO.transpose())
        val dwo = concat.transpose().matMul(delta)
        weightO = optimizerO.adapt(weightO, dwo)

        // Concatの逆伝播（各ヘッドへの勾配に分割）
        val dHeads = Batch(input.size) { batchIndex ->
            val dConcat = dConcat[batchIndex]
            IOType.d3(numOfHeads, outputX, dim) { x, y, z ->
                val index = x * dim + z
                dConcat[y, index]
            }
        }

        // 各ヘッドのScaled-Dot-Attentionの逆伝播
        val dValue = softmax.transpose(axisI = 0, axisJ = 2, axisK = 1).matMul(dHeads)
        val dSoftmax = dHeads.matMul(value.transpose(axisI = 0, axisJ = 2, axisK = 1))

        val sum = (dSoftmax * softmax).sum(axis = 2)
        val dMasked = softmax * (dSoftmax - sum)

        val dScaled = dMasked
        val dMul = dScaled / sqrt(dim.toFloat())

        val dQuery = dMul.matMul(key)
        val dKey = dMul.transpose(axisI = 0, axisJ = 2, axisK = 1).matMul(query)

        // Affineの逆伝播（各ヘッドのQ, K, V）
        val inputT = input.transpose()
        val dQueryD2 = dQuery.reshapeToD2(listOf(outputX, numOfHeads * dim))
        val dxq = dQueryD2.matMul(weightQ.transpose())
        val dwq = inputT.matMul(dQueryD2)

        val dKeyD2 = dKey.reshapeToD2(listOf(outputX, numOfHeads * dim))
        val dxk = dKeyD2.matMul(weightK.transpose())
        val dwk = inputT.matMul(dKeyD2)

        val dValueD2 = dValue.reshapeToD2(listOf(outputX, numOfHeads * dim))
        val dxv = dValueD2.matMul(weightV.transpose())
        val dwv = inputT.matMul(dValueD2)

        weightQ = optimizerQ.adapt(weightQ, dwq)
        weightK = optimizerK.adapt(weightK, dwk)
        weightV = optimizerV.adapt(weightV, dwv)

        return dxq + dxk + dxv
    }

    private fun Context.generatePaddingMask(): Batch<IOType.D2> = if (maskValue == null) {
        Batch(input.size) { IOType.d2(outputX, outputX) { _, _ -> 0f } }
    } else {
        @Suppress("UNCHECKED_CAST")
        val input = input as Batch<IOType.D1>
        Batch(input.size) { index ->
            val input = input[index]
            IOType
                .d1(outputX) { if (input[it] == maskValue.toFloat()) -1e9f else 0f }
                .broadcastToD2(axis = 0, outputX)
        }
    }
}

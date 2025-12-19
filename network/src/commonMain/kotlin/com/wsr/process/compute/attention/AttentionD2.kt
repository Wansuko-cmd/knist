package com.wsr.process.compute.attention

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.get
import com.wsr.batch.math.softmax
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.matmul.matMul
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.batch.reshape.reshape.reshapeToD2
import com.wsr.batch.reshape.reshape.reshapeToD3
import com.wsr.batch.reshape.transpose.transpose
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.reshape.broadcast.broadcastToD2
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlin.math.sqrt
import kotlinx.serialization.Serializable

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val numOfHeads: Int,
    private val dim: Int,
    private val maskValue: Int? = null,
    private var weightQ: IOType.D2,
    private var weightK: IOType.D2,
    private var weightV: IOType.D2,
    private val optimizerQ: Optimizer.D2,
    private val optimizerK: Optimizer.D2,
    private val optimizerV: Optimizer.D2,
    private var weightO: IOType.D2,
    private val optimizerO: Optimizer.D2,
) : Compute.D2() {
    private val mask by lazy { IOType.d2(outputX, outputX) { x, y -> if (x < y) -1e9f else 0f } }
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val query = input.matMul(weightQ)
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = scaled + mask + context.generatePaddingMask()
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = Batch(input.size) { batchIndex ->
            val heads = heads[batchIndex]
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                val headIndex = y / dim
                val dimIndex = y % dim
                heads[headIndex, x, dimIndex]
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
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(i = outputX, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = scaled + mask + context.generatePaddingMask()
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = Batch(input.size) { batchIndex ->
            val heads = heads[batchIndex]
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                val headIndex = y / dim
                val dimIndex = y % dim
                heads[headIndex, x, dimIndex]
            }
        }

        val output = concat.matMul(weightO)
        val delta = calcDelta(output)

        // 出力変換（weightO）の逆伝播
        val dConcat = delta.matMul(weightO, transB = true)
        val dwo = concat.matMul(delta, transA = true)
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
        val dValue = softmax.matMul(dHeads, transA = true)
        val dSoftmax = dHeads.matMul(value, transB = true)

        val sum = (dSoftmax * softmax).sum(axis = 2)
        val dMasked = softmax * dSoftmax.minus(other = sum, axis1 = 0, axis2 = 1)

        val dScaled = dMasked
        val dMul = dScaled / sqrt(dim.toFloat())

        val dQuery = dMul.matMul(key, transB = true)
        val dKey = query.matMul(dMul, transA = true)

        // Affineの逆伝播（各ヘッドのQ, K, V）
        val dQueryD2 = dQuery
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputX, j = numOfHeads * dim)
        val dxq = dQueryD2.matMul(weightQ, transB = true)
        val dwq = input.matMul(dQueryD2, transA = true)

        val dKeyD2 = dKey
            .transpose(axisI = 2, axisJ = 0, axisK = 1)
            .reshapeToD2(i = outputX, j = numOfHeads * dim)
        val dxk = dKeyD2.matMul(weightK, transB = true)
        val dwk = input.matMul(dKeyD2, transA = true)

        val dValueD2 = dValue
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputX, j = numOfHeads * dim)
        val dxv = dValueD2.matMul(weightV, transB = true)
        val dwv = input.matMul(dValueD2, transA = true)

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
                .broadcastToD2(axis = 1, outputX)
        }
    }
}

fun <T> NetworkBuilder.D2<T>.attention(
    numOfHeads: Int,
    dim: Int = inputY / numOfHeads,
    maskValue: Int? = null,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
): NetworkBuilder.D2<T> = addProcess(
    process = AttentionD2(
        outputX = inputX,
        outputY = inputY,
        numOfHeads = numOfHeads,
        dim = dim,
        maskValue = maskValue,
        weightQ = initializer.d2(
            input = listOf(inputY),
            output = listOf(numOfHeads * dim),
            x = inputY,
            y = numOfHeads * dim,
        ),
        weightK = initializer.d2(
            input = listOf(inputY),
            output = listOf(numOfHeads * dim),
            x = inputY,
            y = numOfHeads * dim,
        ),
        weightV = initializer.d2(
            input = listOf(inputY),
            output = listOf(numOfHeads * dim),
            x = inputY,
            y = numOfHeads * dim,
        ),
        weightO = initializer.d2(
            input = listOf(numOfHeads * dim),
            output = listOf(inputY),
            x = numOfHeads * dim,
            y = inputY,
        ),
        optimizerQ = optimizer.d2(inputY, numOfHeads * dim),
        optimizerK = optimizer.d2(inputY, numOfHeads * dim),
        optimizerV = optimizer.d2(inputY, numOfHeads * dim),
        optimizerO = optimizer.d2(numOfHeads * dim, inputY),
    ),
)

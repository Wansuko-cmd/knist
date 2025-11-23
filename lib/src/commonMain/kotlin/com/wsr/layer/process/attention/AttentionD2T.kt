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
import com.wsr.batch.reshape.reshapeToD3
import com.wsr.batch.reshape.transpose
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.matmul.matMul
import com.wsr.core.operation.plus.plus
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

    private var weightQ2: IOType.D2,
    private var weightK2: IOType.D2,
    private var weightV2: IOType.D2,

    private var weightQ: List<IOType.D2>,
    private var weightK: List<IOType.D2>,
    private var weightV: List<IOType.D2>,
    private var weightO: IOType.D2,
    private val optimizerQ: List<Optimizer.D2>,
    private val optimizerK: List<Optimizer.D2>,
    private val optimizerV: List<Optimizer.D2>,
    private val optimizerO: Optimizer.D2,
) : Process.D2() {
    private val mask by lazy { IOType.d2(outputX, outputX) { x, y -> if (x < y) -1e9f else 0f } }
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val query = input.matMul(weightQ2)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK2)
            .reshapeToD3(listOf(outputX, numOfHeads, dim))
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV2)
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
        val query = List(numOfHeads) { input.matMul(weightQ[it]) }
        val key = List(numOfHeads) { input.matMul(weightK[it]) }
        val value = List(numOfHeads) { input.matMul(weightV[it]) }

        val softmax = List(numOfHeads) {
            val mul = query[it].matMul(key[it].transpose())
            val scaled = mul / sqrt(dim.toFloat())
            val masked = scaled + mask + context.generatePaddingMask()
            masked.softmax(axis = 1)
        }

        val heads = List(numOfHeads) { softmax[it].matMul(value[it]) }

        val concat = List(input.size) {
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                heads[y / dim][it][x, y % dim]
            }
        }
        val output = concat.matMul(weightO)

        val delta = calcDelta(output.toBatch()).toList()

        // 出力変換（weightO）の逆伝播
        val dConcat = delta.matMul(weightO.transpose())
        val dwo = concat.transpose().matMul(delta)
        weightO = optimizerO.adapt(weightO, dwo.toBatch())

        // Concatの逆伝播（各ヘッドへの勾配に分割）
        val dHeads = List(numOfHeads) { headIndex ->
            Batch(input.size) { batchIndex ->
                IOType.d2(outputX, dim) { x, y ->
                    val index = headIndex * dim + y
                    dConcat[batchIndex][x, index]
                }
            }
        }

        // 各ヘッドのScaled-Dot-Attentionの逆伝播
        val dValue = List(numOfHeads) { softmax[it].transpose().matMul(dHeads[it]) }
        val dSoftmax = List(numOfHeads) { dHeads[it].matMul(value[it].transpose()) }

        val sum = List(numOfHeads) { (dSoftmax[it] * softmax[it]).sum(axis = 1) }
        val dMasked = List(numOfHeads) { im -> softmax[im] * (dSoftmax[im] - sum[im]) }

        val dScaled = dMasked
        val dMul = List(numOfHeads) { dScaled[it] / sqrt(dim.toFloat()) }

        val dQuery = List(numOfHeads) { dMul[it].matMul(key[it]) }
        val dKey = List(numOfHeads) { dMul[it].transpose().matMul(query[it]) }

        // Affineの逆伝播（各ヘッドのQ, K, V）
        val inputT = input.transpose()
        val dxq = List(numOfHeads) { n -> dQuery[n].matMul(weightQ[n].transpose()) }
        val dwq = List(numOfHeads) { n -> inputT.matMul(dQuery[n]) }

        val dxk = List(numOfHeads) { n -> dKey[n].matMul(weightK[n].transpose()) }
        val dwk = List(numOfHeads) { n -> inputT.matMul(dKey[n]) }

        val dxv = List(numOfHeads) { n -> dValue[n].matMul(weightV[n].transpose()) }
        val dwv = List(numOfHeads) { n -> inputT.matMul(dValue[n]) }

        // 重みの更新
        weightQ = List(numOfHeads) { optimizerQ[it].adapt(weightQ[it], dwq[it]) }
        weightK = List(numOfHeads) { optimizerK[it].adapt(weightK[it], dwk[it]) }
        weightV = List(numOfHeads) { optimizerV[it].adapt(weightV[it], dwv[it]) }

        // dx
        return Batch(input.size) { batchIndex ->
            (0 until numOfHeads)
                .fold(IOType.d2(outputX, outputY) { _, _ -> 0f }) { acc, headIndex ->
                    acc + dxq[headIndex][batchIndex] + dxk[headIndex][batchIndex] + dxv[headIndex][batchIndex]
                }
        }
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

package com.wsr.layer.process.attention

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.div.div
import com.wsr.batch.func.softmax
import com.wsr.batch.matmul.matMul
import com.wsr.batch.minus.minus
import com.wsr.batch.plus.plus
import com.wsr.batch.reshape.transpose
import com.wsr.batch.sum.sum
import com.wsr.batch.times.times
import com.wsr.dot.matmul.matMul
import com.wsr.get
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.broadcastToD2
import com.wsr.reshape.transpose
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.sqrt
import kotlinx.serialization.Serializable

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val numOfHeads: Int,
    private val dim: Int,
    val maskValue: Int? = null,
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
        val heads = List(numOfHeads) {
            val query = input.matMul(weightQ[it])
            val key = input.matMul(weightK[it])
            val value = input.matMul(weightV[it])

            val mul = query.matMul(key.transpose())
            val scaled = mul / sqrt(dim.toFloat())
            val masked = scaled + mask + context.generatePaddingMask()
            val softmax = masked.softmax(axis = 1)
            softmax.matMul(value)
        }
        val concat = List(input.size) {
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                heads[y / dim][it][x, y % dim]
            }
        }
        return concat.matMul(weightO).toBatch()
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
        weightQ = List(numOfHeads) {
            initializer.d2(
                input = listOf(inputY),
                output = listOf(dim),
                x = inputY,
                y = dim,
            )
        },
        weightK = List(numOfHeads) {
            initializer.d2(
                input = listOf(inputY),
                output = listOf(dim),
                x = inputY,
                y = dim,
            )
        },
        weightV = List(numOfHeads) {
            initializer.d2(
                input = listOf(inputY),
                output = listOf(dim),
                x = inputY,
                y = dim,
            )
        },
        weightO = initializer.d2(
            input = listOf(numOfHeads * dim),
            output = listOf(inputY),
            x = numOfHeads * dim,
            y = inputY,
        ),
        optimizerQ = List(numOfHeads) { optimizer.d2(inputY, dim) },
        optimizerK = List(numOfHeads) { optimizer.d2(inputY, dim) },
        optimizerV = List(numOfHeads) { optimizer.d2(inputY, dim) },
        optimizerO = optimizer.d2(numOfHeads * dim, inputY),
    ),
)

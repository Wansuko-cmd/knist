package com.wsr.layer.process.attention

import com.wsr.IOType
import com.wsr.NetworkBuilder
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
    private val dim: Int,
    private var weightQ: List<IOType.D3>,
    private var weightK: List<IOType.D3>,
    private var weightV: List<IOType.D3>,
    private var weightO: IOType.D3,
    private val optimizerQ: List<Optimizer.D3>,
    private val optimizerK: List<Optimizer.D3>,
    private val optimizerV: List<Optimizer.D3>,
    private val optimizerO: Optimizer.D3,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val heads = List(numOfHeads) {
            val query = affine(input, weightQ[it])
            val key = affine(input, weightK[it])
            val value = affine(input, weightV[it])

            val mul = query.matMul(key.transpose())
            val scaled = mul / sqrt(dim.toDouble())

            val mask = IOType.d2(outputX, outputX) { x, y -> if (x > y) -1e9 else 0.0 }
            val masked = scaled + mask

            val softmax = softmax(masked)
            softmax.matMul(value)
        }
        val concat = List(input.size) {
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                heads[y / dim][it][x, y % dim]
            }
        }
        return affine(concat, weightO)
    }

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val query = List(numOfHeads) { affine(input, weightQ[it]) }
        val key = List(numOfHeads) { affine(input, weightK[it]) }
        val value = List(numOfHeads) { affine(input, weightV[it]) }

        val softmax = List(numOfHeads) {
            val mul = query[it].matMul(key[it].transpose())
            val scaled = mul / sqrt(dim.toDouble())

            val mask = IOType.d2(outputX, outputX) { x, y -> if (x > y) -1e9 else 0.0 }
            val masked = scaled + mask

            softmax(masked)
        }

        val heads = List(numOfHeads) { softmax[it].matMul(value[it]) }

        val concat = List(input.size) {
            IOType.d2(outputX, numOfHeads * dim) { x, y ->
                heads[y / dim][it][x, y % dim]
            }
        }
        val output = affine(concat, weightO)

        val delta = calcDelta(output)

        // 出力変換（weightO）の逆伝播
        val dConcat = delta.map { d -> (0 until outputX).map { weightO[it].matMul(d[it]) }.toD2() }
        val concatD3 = concat.toD3().transpose(1, 2, 0)
        val deltaD3 = delta.toD3().transpose(1, 0, 2)
        val dwo = (0 until outputX).map { concatD3[it].matMul(deltaD3[it]) }.toD3() / input.size.toDouble()
        weightO = optimizerO.adapt(weightO, dwo)

        // Concatの逆伝播（各ヘッドへの勾配に分割）
        val dHeads = List(numOfHeads) { headIndex ->
            List(input.size) { batchIndex ->
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
        val dMasked = List(numOfHeads) { im ->
            List(input.size) { i ->
                IOType.d2(outputX, outputX) { x, y ->
                    softmax[im][i][x, y] * (dSoftmax[im][i][x, y] - sum[im][i][x])
                }
            }
        }

        val dScaled = dMasked
        val dMul = List(numOfHeads) { dScaled[it] / sqrt(dim.toDouble()) }

        val dQuery = List(numOfHeads) { dMul[it].matMul(key[it]) }
        val dKey = List(numOfHeads) { dMul[it].transpose().matMul(query[it]) }

        // Affineの逆伝播（各ヘッドのQ, K, V）
        val dwi = input.toD3().transpose(1, 2, 0)

        val dxq = List(numOfHeads) { n ->
            dQuery[n]
                .map { delta ->
                    (0 until outputX)
                        .map { weightQ[n][it].matMul(delta[it]) }
                        .toD2()
                }
        }
        val dqw = List(numOfHeads) { dQuery[it].toD3().transpose(1, 0, 2) }
        val dwq = List(numOfHeads) { n ->
            (0 until outputX)
                .map { dwi[it].matMul(dqw[n][it]) }
                .toD3() / input.size.toDouble()
        }

        val dxk = List(numOfHeads) { n ->
            dKey[n]
                .map { delta ->
                    (0 until outputX)
                        .map { weightK[n][it].matMul(delta[it]) }
                        .toD2()
                }
        }
        val dkw = List(numOfHeads) { dKey[it].toD3().transpose(1, 0, 2) }
        val dwk = List(numOfHeads) { n ->
            (0 until outputX)
                .map { dwi[it].matMul(dkw[n][it]) }
                .toD3() / input.size.toDouble()
        }

        val dxv = List(numOfHeads) { n ->
            dValue[n]
                .map { delta ->
                    (0 until outputX)
                        .map { weightV[n][it].matMul(delta[it]) }
                        .toD2()
                }
        }
        val dvw = List(numOfHeads) { dValue[it].toD3().transpose(1, 0, 2) }
        val dwv = List(numOfHeads) { n ->
            (0 until outputX)
                .map { dwi[it].matMul(dvw[n][it]) }
                .toD3() / input.size.toDouble()
        }

        // 重みの更新
        weightQ = List(numOfHeads) { optimizerQ[it].adapt(weightQ[it], dwq[it]) }
        weightK = List(numOfHeads) { optimizerK[it].adapt(weightK[it], dwk[it]) }
        weightV = List(numOfHeads) { optimizerV[it].adapt(weightV[it], dwv[it]) }

        // dx
        return List(input.size) { batchIndex ->
            (0 until numOfHeads)
                .fold(IOType.d2(outputX, outputY) { _, _ -> 0.0 }) { acc, headIndex ->
                    acc + dxq[headIndex][batchIndex] + dxk[headIndex][batchIndex] + dxv[headIndex][batchIndex]
                }
        }
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

fun <T> NetworkBuilder.D2<T>.attention(
    numOfHeads: Int,
    dim: Int = inputY / numOfHeads,
): NetworkBuilder.D2<T> {
    return addProcess(
        process = AttentionD2(
            outputX = inputX,
            outputY = inputY,
            numOfHeads = numOfHeads,
            dim = dim,
            weightQ = List(numOfHeads) {
                IOType.d3(inputX, inputY, dim) { _, _, _ ->
                    random.nextDouble(-1.0, 1.0)
                }
            },
            weightK = List(numOfHeads) {
                IOType.d3(inputX, inputY, dim) { _, _, _ ->
                    random.nextDouble(-1.0, 1.0)
                }
            },
            weightV = List(numOfHeads) {
                IOType.d3(inputX, inputY, dim) { _, _, _ ->
                    random.nextDouble(-1.0, 1.0)
                }
            },
            weightO = IOType.d3(inputX, numOfHeads * dim, inputY) { _, _, _ ->
                random.nextDouble(-1.0, 1.0)
            },
            optimizerQ = List(numOfHeads) { optimizer.d3(inputX, inputY, dim) },
            optimizerK = List(numOfHeads) { optimizer.d3(inputX, inputY, dim) },
            optimizerV = List(numOfHeads) { optimizer.d3(inputX, inputY, dim) },
            optimizerO = optimizer.d3(inputX, numOfHeads * dim, inputY),
        ),
    )
}

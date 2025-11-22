package com.wsr.layer.process.position

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.d1
import com.wsr.d2
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.set
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlinx.serialization.Serializable

@Serializable
class RoPED2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val waveLength: Float,
) : Process.D2() {
    private val theta by lazy {
        IOType.d1(outputY / 2) { i -> 1f / waveLength.pow(2f * i / outputY) }
    }

    private val cosCache by lazy {
        IOType.d2(outputX, outputY / 2) { x, y -> cos(x * theta[y]) }
    }

    private val sinCache by lazy {
        IOType.d2(outputX, outputY / 2) { x, y -> sin(x * theta[y]) }
    }

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.applyRoPE()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input.applyRoPE()
        val delta = calcDelta(output)
        return delta.applyRoPE()
    }

    private fun Batch<IOType.D2>.applyRoPE() = map { input ->
        IOType.d2(outputX, outputY) { pos, dim ->
            val pairIndex = dim / 2
            val cosVal = cosCache[pos][pairIndex]
            val sinVal = sinCache[pos][pairIndex]

            if (dim % 2 == 0) {
                // 偶数次元: x * cos(θ) - y * sin(θ)
                val x = input[pos, dim]
                val y = input[pos, dim + 1]
                x * cosVal - y * sinVal
            } else {
                // 奇数次元: x * sin(θ) + y * cos(θ)
                val x = input[pos, dim - 1]
                val y = input[pos, dim]
                x * sinVal + y * cosVal
            }
        }
    }
}

fun <T> NetworkBuilder.D2<T>.roPE(waveLength: Float = 10000f) = addProcess(
    process = RoPED2(
        outputX = inputX,
        outputY = inputY,
        waveLength = waveLength,
    ),
)

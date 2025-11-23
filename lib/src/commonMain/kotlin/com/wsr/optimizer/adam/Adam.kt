package com.wsr.optimizer.adam

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.math.pow
import com.wsr.core.math.sqrt
import com.wsr.core.operation.div.div
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times
import com.wsr.optimizer.Optimizer
import kotlin.math.pow
import kotlinx.serialization.Serializable

private const val E = 1e-8f

@Serializable
data class Adam(
    private val rate: Float,
    private val momentum: Float = 0.9f,
    private val rms: Float = 0.999f,
    private val maxNorm: Float = Float.MAX_VALUE,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = AdamD1(
        rate = rate,
        momentum = momentum,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = AdamD2(
        rate = rate,
        momentum = momentum,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = AdamD3(
        rate = rate,
        momentum = momentum,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(x, y, z),
    )
}

@Serializable
internal data class AdamD1(
    private val rate: Float,
    private val momentum: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D1(maxNorm) {
    private var m: IOType.D1 = IOType.d1(shape)
    private var v: IOType.D1 = IOType.d1(shape)
    private var t: Int = 0

    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1f - momentum.pow(t.toFloat()))
        val vHat = v / (1f - rms.pow(t.toFloat()))

        return weight - rate * mHat / vHat.sqrt(e = E)
    }
}

@Serializable
internal data class AdamD2(
    private val rate: Float,
    private val momentum: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D2(maxNorm) {
    private var m: IOType.D2 = IOType.d2(shape)
    private var v: IOType.D2 = IOType.d2(shape)
    private var t: Int = 0

    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1f - momentum.pow(t.toFloat()))
        val vHat = v / (1f - rms.pow(t.toFloat()))

        return weight - rate * mHat / vHat.sqrt(e = E)
    }
}

@Serializable
internal data class AdamD3(
    private val rate: Float,
    private val momentum: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D3(maxNorm) {
    private var m: IOType.D3 = IOType.d3(shape)
    private var v: IOType.D3 = IOType.d3(shape)
    private var t: Int = 0

    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1f - momentum.pow(t.toFloat()))
        val vHat = v / (1f - rms.pow(t.toFloat()))

        return weight - rate * mHat / vHat.sqrt(e = E)
    }
}

package com.wsr.optimizer.adam

import com.wsr.IOType
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.pow.pow
import com.wsr.sqrt.sqrt
import kotlin.math.pow
import kotlinx.serialization.Serializable

private const val E = 1e-8

@Serializable
data class Adam(private val rate: Double, private val momentum: Double = 0.9, private val rms: Double = 0.999) :
    Optimizer {
    override fun d1(size: Int): Optimizer.D1 = AdamD1(
        rate = rate,
        momentum = momentum,
        rms = rms,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = AdamD2(
        rate = rate,
        momentum = momentum,
        rms = rms,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = AdamD3(
        rate = rate,
        momentum = momentum,
        rms = rms,
        shape = listOf(x, y, z),
    )
}

@Serializable
internal data class AdamD1(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
    private val shape: List<Int>,
) : Optimizer.D1 {
    private var m: IOType.D1 = IOType.d1(shape)
    private var v: IOType.D1 = IOType.d1(shape)
    private var t: Int = 0

    override fun adapt(dw: IOType.D1): IOType.D1 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d1(dw.shape) { E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

@Serializable
internal data class AdamD2(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
    private val shape: List<Int>,
) : Optimizer.D2 {
    private var m: IOType.D2 = IOType.d2(shape)
    private var v: IOType.D2 = IOType.d2(shape)
    private var t: Int = 0

    override fun adapt(dw: IOType.D2): IOType.D2 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d2(dw.shape) { _, _ -> E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

@Serializable
internal data class AdamD3(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
    private val shape: List<Int>,
) : Optimizer.D3 {
    private var m: IOType.D3 = IOType.d3(shape)
    private var v: IOType.D3 = IOType.d3(shape)
    private var t: Int = 0

    override fun adapt(dw: IOType.D3): IOType.D3 {
        t += 1

        m = momentum * m + (1 - momentum) * dw
        v = rms * v + (1 - rms) * dw.pow(2)

        val mHat = m / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d3(dw.shape) { _, _, _ -> E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

package com.wsr.optimizer.adam

import com.wsr.IOType
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.pow.pow
import com.wsr.sqrt.sqrt
import kotlinx.serialization.Serializable
import kotlin.math.pow

private const val E = 1e-8

@Serializable
data class Adam(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
) : Optimizer {
    override fun d1(): Optimizer.D1 = AdamD1(rate, momentum, rms)

    override fun d2(): Optimizer.D2 = AdamD2(rate, momentum, rms)

    override fun d3(): Optimizer.D3 = AdamD3(rate, momentum, rms)
}

@Serializable
internal data class AdamD1(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
) : Optimizer.D1 {
    private var m: IOType.D1? = null
    private var v: IOType.D1? = null
    private var t: Int = 0

    override fun adapt(dw: IOType.D1): IOType.D1 {
        t += 1

        m = momentum * (m ?: IOType.d1(dw.shape)) + (1 - momentum) * dw
        v = rms * (v ?: IOType.d1(dw.shape)) + (1 - rms) * dw.pow(2)

        val mHat = m!! / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v!! / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d1(dw.shape) { E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

@Serializable
internal data class AdamD2(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
) : Optimizer.D2 {
    private var m: IOType.D2? = null
    private var v: IOType.D2? = null
    private var t: Int = 0

    override fun adapt(dw: IOType.D2): IOType.D2 {
        t += 1

        m = momentum * (m ?: IOType.d2(dw.shape)) + (1 - momentum) * dw
        v = rms * (v ?: IOType.d2(dw.shape)) + (1 - rms) * dw.pow(2)

        val mHat = m!! / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v!! / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d2(dw.shape) { _, _ -> E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

@Serializable
internal data class AdamD3(
    private val rate: Double,
    private val momentum: Double,
    private val rms: Double,
) : Optimizer.D3 {
    private var m: IOType.D3? = null
    private var v: IOType.D3? = null
    private var t: Int = 0

    override fun adapt(dw: IOType.D3): IOType.D3 {
        t += 1

        m = momentum * (m ?: IOType.d3(dw.shape)) + (1 - momentum) * dw
        v = rms * (v ?: IOType.d3(dw.shape)) + (1 - rms) * dw.pow(2)

        val mHat = m!! / (1.0 - momentum.pow(t.toDouble()))
        val vHat = v!! / (1.0 - rms.pow(t.toDouble()))

        val e = IOType.d3(dw.shape) { _, _, _ -> E }
        return rate / (vHat.sqrt() + e) * mHat
    }
}

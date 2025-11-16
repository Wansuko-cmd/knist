package com.wsr.optimizer.rms

import com.wsr.IOType
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.power.pow
import com.wsr.power.sqrt
import kotlinx.serialization.Serializable

private const val E = 1e-8f

@Serializable
data class RmsProp(
    private val rate: Float,
    private val rms: Float = 0.9f,
    private val maxNorm: Float = Float.MAX_VALUE,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = RmsPropD1(
        rate = rate,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = RmsPropD2(
        rate = rate,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = RmsPropD3(
        rate = rate,
        rms = rms,
        maxNorm = maxNorm,
        shape = listOf(x, y, z),
    )
}

@Serializable
internal data class RmsPropD1(
    private val rate: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D1(maxNorm) {
    private var velocity: IOType.D1 = IOType.d1(shape)
    private val e = IOType.d1(shape) { E }

    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - rate / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD2(
    private val rate: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D2(maxNorm) {
    private var velocity: IOType.D2 = IOType.d2(shape)
    private val e = IOType.d2(shape) { _, _ -> E }

    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - rate / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD3(
    private val rate: Float,
    private val rms: Float,
    private val maxNorm: Float,
    private val shape: List<Int>,
) : Optimizer.D3(maxNorm) {
    private var velocity: IOType.D3 = IOType.d3(shape)
    private val e = IOType.d3(shape) { _, _, _ -> E }

    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - rate / (velocity.sqrt() + e) * dw
    }
}

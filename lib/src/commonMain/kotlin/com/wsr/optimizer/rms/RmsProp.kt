package com.wsr.optimizer.rms

import com.wsr.IOType
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.pow.pow
import com.wsr.sqrt.sqrt
import kotlinx.serialization.Serializable

private const val E = 1e-8

@Serializable
data class RmsProp(
    private val rate: Double,
    private val rms: Double = 0.9,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = RmsPropD1(
        rate = rate,
        rms = rms,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = RmsPropD2(
        rate = rate,
        rms = rms,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = RmsPropD3(
        rate = rate,
        rms = rms,
        shape = listOf(x, y, z),
    )
}

@Serializable
internal data class RmsPropD1(private val rate: Double, private val rms: Double, private val shape: List<Int>) :
    Optimizer.D1 {
    private var velocity: IOType.D1 = IOType.d1(shape)
    override fun adapt(dw: IOType.D1): IOType.D1 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        val e = IOType.d1(dw.shape) { E }
        return rate / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD2(private val rate: Double, private val rms: Double, private val shape: List<Int>) :
    Optimizer.D2 {
    private var velocity: IOType.D2 = IOType.d2(shape)
    override fun adapt(dw: IOType.D2): IOType.D2 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        val e = IOType.d2(dw.shape) { _, _ -> E }
        return rate / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD3(private val rate: Double, private val rms: Double, private val shape: List<Int>) :
    Optimizer.D3 {
    private var velocity: IOType.D3 = IOType.d3(shape)
    override fun adapt(dw: IOType.D3): IOType.D3 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        val e = IOType.d3(dw.shape) { _, _, _ -> E }
        return rate / (velocity.sqrt() + e) * dw
    }
}

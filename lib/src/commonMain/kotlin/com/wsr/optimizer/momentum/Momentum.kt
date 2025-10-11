package com.wsr.optimizer.momentum

import com.wsr.IOType
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
data class Momentum(private val rate: Double, private val momentum: Double) : Optimizer {
    override fun d1(): Optimizer.D1 = MomentumD1(rate, momentum)

    override fun d2(): Optimizer.D2 = MomentumD2(rate, momentum)

    override fun d3(): Optimizer.D3 = MomentumD3(rate, momentum)
}

@Serializable
internal data class MomentumD1(private val rate: Double, private val momentum: Double) : Optimizer.D1 {
    private var velocity: IOType.D1? = null
    override fun adapt(dw: IOType.D1): IOType.D1 {
        velocity = momentum * (velocity ?: IOType.d1(dw.shape)) + dw
        return rate * velocity!!
    }
}

@Serializable
internal data class MomentumD2(private val rate: Double, private val momentum: Double) : Optimizer.D2 {
    private var velocity: IOType.D2? = null
    override fun adapt(dw: IOType.D2): IOType.D2 {
        velocity = momentum * (velocity ?: IOType.d2(dw.shape)) + dw
        return rate * velocity!!
    }
}

@Serializable
internal data class MomentumD3(private val rate: Double, private val momentum: Double) : Optimizer.D3 {
    private var velocity: IOType.D3? = null
    override fun adapt(dw: IOType.D3): IOType.D3 {
        velocity = momentum * (velocity ?: IOType.d3(dw.shape)) + dw
        return rate * velocity!!
    }
}

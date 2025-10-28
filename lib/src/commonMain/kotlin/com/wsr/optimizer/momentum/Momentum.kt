package com.wsr.optimizer.momentum

import com.wsr.IOType
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
data class Momentum(private val rate: Double, private val momentum: Double = 0.9) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = MomentumD1(
        rate = rate,
        momentum = momentum,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = MomentumD2(
        rate = rate,
        momentum = momentum,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = MomentumD3(
        rate = rate,
        momentum = momentum,
        shape = listOf(x, y, z),
    )
}

@Serializable
internal data class MomentumD1(private val rate: Double, private val momentum: Double, private val shape: List<Int>) :
    Optimizer.D1() {
    private var velocity: IOType.D1 = IOType.d1(shape)
    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
        velocity = momentum * velocity + dw
        return weight - rate * velocity
    }
}

@Serializable
internal data class MomentumD2(private val rate: Double, private val momentum: Double, private val shape: List<Int>) :
    Optimizer.D2() {
    private var velocity: IOType.D2 = IOType.d2(shape)
    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 {
        velocity = momentum * velocity + dw
        return weight - rate * velocity
    }
}

@Serializable
internal data class MomentumD3(private val rate: Double, private val momentum: Double, private val shape: List<Int>) :
    Optimizer.D3() {
    private var velocity: IOType.D3 = IOType.d3(shape)
    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 {
        velocity = momentum * velocity + dw
        return weight - rate * velocity
    }
}

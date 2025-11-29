package com.wsr.optimizer.momentum

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times
import com.wsr.optimizer.Optimizer
import com.wsr.optimizer.Scheduler
import kotlinx.serialization.Serializable

@Serializable
data class Momentum(
    private val scheduler: Scheduler,
    private val momentum: Float = 0.9f,
    private val maxNorm: Float = Float.MAX_VALUE,
    private val stepUnit: Int = 1,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = MomentumD1(
        scheduler = scheduler,
        momentum = momentum,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(size),
    )

    override fun d2(i: Int, j: Int): Optimizer.D2 = MomentumD2(
        scheduler = scheduler,
        momentum = momentum,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(i, j),
    )

    override fun d3(i: Int, j: Int, k: Int): Optimizer.D3 = MomentumD3(
        scheduler = scheduler,
        momentum = momentum,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(i, j, k),
    )

    override fun d4(i: Int, j: Int, k: Int, l: Int): Optimizer.D4 = MomentumD4(
        scheduler = scheduler,
        momentum = momentum,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(i, j, k, l),
    )
}

@Serializable
internal data class MomentumD1(
    private val scheduler: Scheduler,
    private val momentum: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D1(maxNorm, stepUnit) {
    private var velocity: IOType.D1 = IOType.d1(shape)
    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
        velocity = momentum * velocity + dw
        return weight - scheduler.calcRate(step = step) * velocity
    }
}

@Serializable
internal data class MomentumD2(
    private val scheduler: Scheduler,
    private val momentum: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D2(maxNorm, stepUnit) {
    private var velocity: IOType.D2 = IOType.d2(shape)
    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 {
        velocity = momentum * velocity + dw
        return weight - scheduler.calcRate(step = step) * velocity
    }
}

@Serializable
internal data class MomentumD3(
    private val scheduler: Scheduler,
    private val momentum: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D3(maxNorm, stepUnit) {
    private var velocity: IOType.D3 = IOType.d3(shape)
    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 {
        velocity = momentum * velocity + dw
        return weight - scheduler.calcRate(step = step) * velocity
    }
}

@Serializable
internal data class MomentumD4(
    private val scheduler: Scheduler,
    private val momentum: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D4(maxNorm, stepUnit) {
    private var velocity: IOType.D4 = IOType.d4(shape)
    override fun adapt(weight: IOType.D4, dw: IOType.D4): IOType.D4 {
        velocity = momentum * velocity + dw
        return weight - scheduler.calcRate(step = step) * velocity
    }
}

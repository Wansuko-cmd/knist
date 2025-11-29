package com.wsr.optimizer.rms

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.math.pow
import com.wsr.core.math.sqrt
import com.wsr.core.operation.div.div
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times
import com.wsr.optimizer.Optimizer
import com.wsr.optimizer.Scheduler
import kotlinx.serialization.Serializable

private const val E = 1e-8f

@Serializable
data class RmsProp(
    private val scheduler: Scheduler,
    private val rms: Float = 0.9f,
    private val maxNorm: Float = Float.MAX_VALUE,
    private val stepUnit: Int = 1,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = RmsPropD1(
        scheduler = scheduler,
        rms = rms,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(size),
    )

    override fun d2(x: Int, y: Int): Optimizer.D2 = RmsPropD2(
        scheduler = scheduler,
        rms = rms,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(x, y),
    )

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = RmsPropD3(
        scheduler = scheduler,
        rms = rms,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(x, y, z),
    )

    override fun d4(i: Int, j: Int, k: Int, l: Int): Optimizer.D4 = RmsPropD4(
        scheduler = scheduler,
        rms = rms,
        maxNorm = maxNorm,
        stepUnit = stepUnit,
        shape = listOf(i, j, k, l),
    )
}

@Serializable
internal data class RmsPropD1(
    private val scheduler: Scheduler,
    private val rms: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D1(maxNorm, stepUnit) {
    private var velocity: IOType.D1 = IOType.d1(shape)
    private val e = IOType.d1(shape) { E }

    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - scheduler.calcRate(step = step) / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD2(
    private val scheduler: Scheduler,
    private val rms: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D2(maxNorm, stepUnit) {
    private var velocity: IOType.D2 = IOType.d2(shape)
    private val e = IOType.d2(shape) { _, _ -> E }

    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - scheduler.calcRate(step = step) / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD3(
    private val scheduler: Scheduler,
    private val rms: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D3(maxNorm, stepUnit) {
    private var velocity: IOType.D3 = IOType.d3(shape)
    private val e = IOType.d3(shape) { _, _, _ -> E }

    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - scheduler.calcRate(step = step) / (velocity.sqrt() + e) * dw
    }
}

@Serializable
internal data class RmsPropD4(
    private val scheduler: Scheduler,
    private val rms: Float,
    private val maxNorm: Float,
    private val stepUnit: Int,
    private val shape: List<Int>,
) : Optimizer.D4(maxNorm, stepUnit) {
    private var velocity: IOType.D4 = IOType.d4(shape)
    private val e = IOType.d4(shape) { _, _, _, _ -> E }

    override fun adapt(weight: IOType.D4, dw: IOType.D4): IOType.D4 {
        velocity = rms * velocity + (1 - rms) * dw.pow(2)
        return weight - scheduler.calcRate(step = step) / (velocity.sqrt() + e) * dw
    }
}

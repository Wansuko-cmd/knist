package com.wsr.optimizer.sgd

import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.times.times
import com.wsr.optimizer.Optimizer
import com.wsr.optimizer.Scheduler
import kotlinx.serialization.Serializable

@Serializable
data class Sgd(
    private val scheduler: Scheduler,
    private val maxNorm: Float = Float.MAX_VALUE,
    private val stepUnit: Int = 1,
) : Optimizer {
    override fun d1(size: Int): Optimizer.D1 = SgdD1(scheduler, maxNorm, stepUnit)

    override fun d2(x: Int, y: Int): Optimizer.D2 = SgdD2(scheduler, maxNorm, stepUnit)

    override fun d3(x: Int, y: Int, z: Int): Optimizer.D3 = SgdD3(scheduler, maxNorm, stepUnit)
}

@Serializable
internal data class SgdD1(private val scheduler: Scheduler, private val maxNorm: Float, private val stepUnit: Int) :
    Optimizer.D1(maxNorm, stepUnit) {
    override fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1 = weight - scheduler.calcRate(step = step) * dw
}

@Serializable
internal data class SgdD2(private val scheduler: Scheduler, private val maxNorm: Float, private val stepUnit: Int) :
    Optimizer.D2(maxNorm, stepUnit) {
    override fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2 = weight - scheduler.calcRate(step = step) * dw
}

@Serializable
internal data class SgdD3(private val scheduler: Scheduler, private val maxNorm: Float, private val stepUnit: Int) :
    Optimizer.D3(maxNorm, stepUnit) {
    override fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3 = weight - scheduler.calcRate(step = step) * dw
}

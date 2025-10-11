package com.wsr.optimizer.sgd

import com.wsr.IOType
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
data class Sgd(private val rate: Double) : Optimizer {
    override fun d1(): Optimizer.D1 = SgdD1(rate)

    override fun d2(): Optimizer.D2 = SgdD2(rate)

    override fun d3(): Optimizer.D3 = SgdD3(rate)
}

@Serializable
data class SgdD1(private val rate: Double) : Optimizer.D1 {
    override fun adapt(dw: IOType.D1): IOType.D1 = rate * dw
}

@Serializable
data class SgdD2(private val rate: Double) : Optimizer.D2 {
    override fun adapt(dw: IOType.D2): IOType.D2 = rate * dw
}

@Serializable
data class SgdD3(private val rate: Double) : Optimizer.D3 {
    override fun adapt(dw: IOType.D3): IOType.D3 = rate * dw
}

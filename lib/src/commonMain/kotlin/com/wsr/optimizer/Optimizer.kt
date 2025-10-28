package com.wsr.optimizer

import com.wsr.IOType
import kotlinx.serialization.Serializable

interface Optimizer {
    fun d1(size: Int): D1
    fun d2(x: Int, y: Int): D2
    fun d3(x: Int, y: Int, z: Int): D3

    @Serializable
    abstract class D1 {
        abstract fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1
    }

    @Serializable
    abstract class D2 {
        abstract fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2
    }

    @Serializable
    abstract class D3 {
        abstract fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3
    }
}

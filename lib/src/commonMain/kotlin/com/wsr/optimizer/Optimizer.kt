package com.wsr.optimizer

import com.wsr.IOType

interface Optimizer {
    fun d1(size: Int): D1
    fun d2(x: Int, y: Int): D2
    fun d3(x: Int, y: Int, z: Int): D3

    interface D1 {
        fun adapt(dw: IOType.D1): IOType.D1
    }

    interface D2 {
        fun adapt(dw: IOType.D2): IOType.D2
    }

    interface D3 {
        fun adapt(dw: IOType.D3): IOType.D3
    }
}

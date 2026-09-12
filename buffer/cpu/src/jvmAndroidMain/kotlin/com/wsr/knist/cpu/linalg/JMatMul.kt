package com.wsr.knist.cpu.linalg

object JMatMul {
    external fun inner(x: Long, y: Long, b: Int, result: Long)

    external fun matMulD1ToD2(x: Long, y: Long, transY: Boolean, n: Int, k: Int, result: Long)

    external fun matMulD2ToD1(x: Long, transX: Boolean, y: Long, m: Int, k: Int, result: Long)

    external fun matMulD2ToD2(x: Long, transX: Boolean, y: Long, transY: Boolean, m: Int, n: Int, k: Int, b: Int, result: Long)
}

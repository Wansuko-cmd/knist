package com.wsr.knist.gpu.elementwise.operation

object JMinus {
    external fun minusD0ToD1(x: Float, y: Long, result: Long, runtime: Long)

    external fun minusD1ToD0(x: Long, y: Float, result: Long, runtime: Long)
    external fun minusD1ToD1(x: Long, y: Long, result: Long, runtime: Long)
    external fun minusD1ToD2(x: Long, y: Long, yi: Int, yj: Int, axis: Int, result: Long, runtime: Long)
    external fun minusD1ToD3(x: Long, y: Long, yi: Int, yj: Int, yk: Int, axis: Int, result: Long, runtime: Long)

    external fun minusD2ToD1(x: Long, xi: Int, xj: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun minusD2ToD3(x: Long, xi: Int, xj: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)

    external fun minusD3ToD1(x: Long, xi: Int, xj: Int, xk: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun minusD3ToD2(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)
    external fun minusD3ToD4(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int, result: Long, runtime: Long)

    external fun minusD4ToD1(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun minusD4ToD2(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)
    external fun minusD4ToD3(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int, result: Long, runtime: Long)
}

package com.wsr.knist.gpu.elementwise.operation

object JDiv {
    external fun divD0ToD1(x: Float, y: Long, result: Long, runtime: Long)

    external fun divD1ToD0(x: Long, y: Float, result: Long, runtime: Long)
    external fun divD1ToD1(x: Long, y: Long, result: Long, runtime: Long)
    external fun divD1ToD2(x: Long, y: Long, yi: Int, yj: Int, axis: Int, result: Long, runtime: Long)
    external fun divD1ToD3(x: Long, y: Long, yi: Int, yj: Int, yk: Int, axis: Int, result: Long, runtime: Long)

    external fun divD2ToD1(x: Long, xi: Int, xj: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun divD2ToD3(x: Long, xi: Int, xj: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)

    external fun divD3ToD1(x: Long, xi: Int, xj: Int, xk: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun divD3ToD2(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)
    external fun divD3ToD4(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int, result: Long, runtime: Long)

    external fun divD4ToD1(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, axis: Int, result: Long, runtime: Long)
    external fun divD4ToD2(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long, runtime: Long)
    external fun divD4ToD3(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int, result: Long, runtime: Long)
}

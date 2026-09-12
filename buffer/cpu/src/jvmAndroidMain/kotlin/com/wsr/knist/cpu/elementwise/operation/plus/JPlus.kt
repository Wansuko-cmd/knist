package com.wsr.knist.cpu.elementwise.operation.plus

object JPlus {
    external fun plusD0ToD1(x: Float, y: Long, result: Long)

    external fun plusD1ToD0(x: Long, y: Float, result: Long)
    external fun plusD1ToD1(x: Long, y: Long, result: Long)
    external fun plusD1ToD2(x: Long, y: Long, yi: Int, yj: Int, axis: Int, result: Long)
    external fun plusD1ToD3(x: Long, y: Long, yi: Int, yj: Int, yk: Int, axis: Int, result: Long)

    external fun plusD2ToD1(x: Long, xi: Int, xj: Int, y: Long, axis: Int, result: Long)
    external fun plusD2ToD3(x: Long, xi: Int, xj: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, result: Long)

    external fun plusD3ToD1(x: Long, xi: Int, xj: Int, xk: Int, y: Long, axis: Int, result: Long)
    external fun plusD3ToD2(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long)
    external fun plusD3ToD4(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int, result: Long)

    external fun plusD4ToD1(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, axis: Int, result: Long)
    external fun plusD4ToD2(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long)
    external fun plusD4ToD3(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int, result: Long)
}

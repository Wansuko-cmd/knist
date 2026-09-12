package com.wsr.knist.cpu.elementwise.operation.times

object JTimes {
    external fun timesD0ToD1(x: Float, y: Long, result: Long)

    external fun timesD1ToD0(x: Long, y: Float, result: Long)
    external fun timesD1ToD1(x: Long, y: Long, result: Long)
    external fun timesD1ToD2(x: Long, y: Long, yi: Int, yj: Int, axis: Int, result: Long)
    external fun timesD1ToD3(x: Long, y: Long, yi: Int, yj: Int, yk: Int, axis: Int, result: Long)

    external fun timesD2ToD1(x: Long, xi: Int, xj: Int, y: Long, axis: Int, result: Long)
    external fun timesD2ToD3(x: Long, xi: Int, xj: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, result: Long)

    external fun timesD3ToD1(x: Long, xi: Int, xj: Int, xk: Int, y: Long, axis: Int, result: Long)
    external fun timesD3ToD2(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long)
    external fun timesD3ToD4(x: Long, xi: Int, xj: Int, xk: Int, y: Long, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int, result: Long)

    external fun timesD4ToD1(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, axis: Int, result: Long)
    external fun timesD4ToD2(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, axis1: Int, axis2: Int, result: Long)
    external fun timesD4ToD3(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, y: Long, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int, result: Long)
}

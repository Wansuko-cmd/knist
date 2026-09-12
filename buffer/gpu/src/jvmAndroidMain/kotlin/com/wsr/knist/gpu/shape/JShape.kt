package com.wsr.knist.gpu.shape

object JShape {
    external fun transposeD2(x: Long, xi: Int, xj: Int, result: Long, runtime: Long)

    external fun transposeD3(x: Long, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int, result: Long, runtime: Long)

    external fun transposeD4(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, result: Long, runtime: Long)

    external fun sliceD1(x: Long, start: Int, end: Int, step: Int, result: Long, runtime: Long)

    external fun sliceD2(x: Long, xi: Int, xj: Int, axis: Int, start: Int, end: Int, step: Int, result: Long, runtime: Long)

    external fun sliceD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, start: Int, end: Int, step: Int, result: Long, runtime: Long)

    external fun copyIntoD1(x: Long, result: Long, start: Int, end: Int, step: Int, runtime: Long)

    external fun copyIntoD2(x: Long, result: Long, ri: Int, rj: Int, axis: Int, start: Int, end: Int, step: Int, runtime: Long)

    external fun copyIntoD3(x: Long, result: Long, ri: Int, rj: Int, rk: Int, axis: Int, start: Int, end: Int, step: Int, runtime: Long)

    external fun flipD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long, runtime: Long)

    external fun unfoldD1(x: Long, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int, result: Long, runtime: Long)

    external fun unfoldD2(x: Long, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int, result: Long, runtime: Long)

    external fun foldD1(x: Long, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int, result: Long, runtime: Long)

    external fun foldD2(x: Long, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int, result: Long, runtime: Long)
}

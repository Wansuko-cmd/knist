package com.wsr.knist.cpu.reduction

object JReduction {
    external fun averageD1(x: Long, result: Long)
    external fun averageD2(x: Long, xi: Int, xj: Int, axis: Int, result: Long)
    external fun averageD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long)

    external fun maxD1(x: Long, result: Long)
    external fun maxD2(x: Long, xi: Int, xj: Int, axis: Int, result: Long)
    external fun maxD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long)

    external fun minD1(x: Long, result: Long)
    external fun minD2(x: Long, xi: Int, xj: Int, axis: Int, result: Long)
    external fun minD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long)

    external fun sumD1(x: Long, result: Long)
    external fun sumD2(x: Long, xi: Int, xj: Int, axis: Int, result: Long)
    external fun sumD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long)

    external fun maxIndexD1(x: Long, result: Long)
    external fun maxIndexD2(x: Long, xi: Int, xj: Int, axis: Int, result: Long)
    external fun maxIndexD3(x: Long, xi: Int, xj: Int, xk: Int, axis: Int, result: Long)

    external fun topKD1(x: Long, k: Int, seed: Long, result: Long)
    external fun topKD2(x: Long, xi: Int, xj: Int, k: Int, axis: Int, seed: Long, result: Long)
    external fun topKD3(x: Long, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, seed: Long, result: Long)

    external fun topPD1(x: Long, p: Float, seed: Long, result: Long)
    external fun topPD2(x: Long, xi: Int, xj: Int, p: Float, axis: Int, seed: Long, result: Long)
    external fun topPD3(x: Long, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, seed: Long, result: Long)
}

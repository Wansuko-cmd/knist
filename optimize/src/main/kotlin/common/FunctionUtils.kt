package common

import kotlin.math.exp

fun step(x: Double) = if (x > 0.0) 1.0 else 0.0

fun relu(x: Double) = if (x > 0.0) x else 0.0

fun sigmoid(x: Double) = 1 / (1 + exp(-x))

/**
 * 正方形のkernelじゃないとエラーを投げる
 * また奇数サイズのkernelを想定
 */
fun List<List<Double>>.conv(kernel: List<List<Double>>, f: (Double) -> Double = { it }): List<List<Double>> {
    val output = mutableListOf<List<Double>>()
    for (i in 0 until (this.size - kernel.size)) {
        val o = mutableListOf<Double>()
        for (j in 0 until (this.size - kernel.size)) {
            kernel.indices.sumOf { p ->
                kernel.indices.sumOf { q ->
                    f(this[i + p][j + q] * kernel[p][q])
                }
            }.let { o.add(it) }
        }
        output.add(o)
    }
    return output
}

fun List<List<Double>>.add(other: List<List<Double>>): List<List<Double>> {
    val output = mutableListOf<List<Double>>()
    for ((i, e) in this.withIndex()) {
        val o = mutableListOf<Double>()
        for ((j, _) in e.withIndex()) {
            o.add(this[i][j] + other[i][j])
        }
        output.add(o)
    }
    return output
}

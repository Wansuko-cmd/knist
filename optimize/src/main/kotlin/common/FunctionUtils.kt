package common

import kotlin.math.exp

fun step(x: Double) = if (x > 0.0) 1.0 else 0.0

fun relu(x: Double) = if (x > 0.0) x else 0.0

fun sigmoid(x: Double) = 1 / (1 + exp(-x))

/**
 * 正方形のkernelじゃないとエラーを投げる
 * また奇数サイズのkernelを想定
 */
fun List<List<Double>>.conv(kernel: List<List<Double>>): List<List<Double>> {
    val output = mutableListOf<List<Double>>()
    (0 until (this.size - 2)).map { i ->
        (0 until (this.size - 2)).map { j ->
            kernel.indices.sumOf { p ->
                kernel.indices.sumOf { q ->
                    this[i + p][j + q] * kernel[p][q]
                }
            }
        }.let { output.add(it) }
    }
    return output
}

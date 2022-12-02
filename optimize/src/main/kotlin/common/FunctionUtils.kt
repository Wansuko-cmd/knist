package common

import kotlin.math.exp

fun step(x: Double) = if (x > 0.0) 1.0 else 0.0

fun relu(x: Double) = if (x > 0.0) x else 0.0

fun sigmoid(x: Double) = 1 / (1 + exp(-x))

/**
 * 正方形のkernelじゃないとエラーを投げる
 * また奇数サイズのkernelを想定
 */
inline fun List<List<Double>>.conv(kernel: List<List<Double>>, f: (Double) -> Double = { it }): List<List<Double>> {
    return this
        .map { it.toTypedArray() }.toTypedArray()
        .convArray(kernel.map { it.toTypedArray() }.toTypedArray())
        .map { a -> a.map { f(it) } }
}

inline fun List<List<Double>>.convA(kernel: Array<Array<Double>>, f: (Double) -> Double = { it }): Array<Array<Double>> {
    return this
        .map { it.toTypedArray() }.toTypedArray()
        .convArray(kernel)
        .map { a -> a.map { f(it) }.toTypedArray() }.toTypedArray()
}

// TODO: activation追加
fun Array<Array<Double>>.convArray(kernel: Array<Array<Double>>): Array<Array<Double>> {
    val outputSize = this.size - kernel.size + 1
    val output = Array(outputSize) { Array(outputSize) { 0.0 } }

    for (ku in kernel.indices) {
        for (kv in kernel.indices) {
            for (ou in 0 until outputSize) {
                for (ov in 0 until outputSize) {
                    output[ou][ov] += this[ku + ou][kv + ov] * kernel[ku][kv]
                }
            }
        }
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

fun Array<Array<Double>>.add(other: Array<Array<Double>>) {
    for (i in 0 until this.size) {
        for (j in 0 until this.size) {
            this[i][j] += other[i][j]
        }
    }
}

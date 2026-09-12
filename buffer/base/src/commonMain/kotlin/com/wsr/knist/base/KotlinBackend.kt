package com.wsr.knist.base

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.DataBufferGenerator
import com.wsr.knist.base.data.Default
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.base.data.indices
import com.wsr.knist.base.data.size
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object KotlinBackend : IBackend {
    override val generator: IDataBufferGenerator = Default.generator

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer = y.map { x + it }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer = x.map { it + y }
    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer = x.zipWith(other = y) { a, b -> a + b }
    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a + b }
    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a + b }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a + b }
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a + b }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a + b }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a + b }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        yl = yl,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a + b }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        axis = axis,
    ) { a, b -> a + b }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a + b }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a + b }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer = y.map { x - it }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer = x.map { it - y }
    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer = x.zipWith(other = y) { a, b -> a - b }
    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a - b }
    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a - b }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a - b }
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a - b }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a - b }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a - b }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        yl = yl,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a - b }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        axis = axis,
    ) { a, b -> a - b }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a - b }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a - b }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer = y.map { x * it }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer = x.map { it * y }
    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer = x.zipWith(other = y) { a, b -> a * b }
    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a * b }
    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a * b }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a * b }
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a * b }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a * b }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a * b }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        yl = yl,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a * b }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        axis = axis,
    ) { a, b -> a * b }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a * b }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a * b }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer = y.map { x / it }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer = x.map { it / y }
    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer = x.zipWith(other = y) { a, b -> a / b }
    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a / b }
    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer = x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a / b }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a / b }
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a / b }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a / b }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a / b }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        yl = yl,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a / b }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        axis = axis,
    ) { a, b -> a / b }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a / b }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        xl = xl,
        other = y,
        yi = yi,
        yj = yj,
        yk = yk,
        axis1 = axis1,
        axis2 = axis2,
        axis3 = axis3,
    ) { a, b -> a / b }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = DataBufferGenerator.create(b)
        val stride = x.size / b
        repeat(b) { b ->
            var acc = 0f
            val offset = b * stride
            repeat(stride) { s ->
                acc += x[offset + s] * y[offset + s]
            }
            result[b] = acc
        }
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = DataBufferGenerator.create(n)
        for (j in 0 until n) {
            var sum = 0f
            for (p in 0 until k) {
                val yValue = if (transY) y[j * k + p] else y[p * n + j]
                sum += x[p] * yValue
            }
            result[j] = sum
        }
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = DataBufferGenerator.create(m)
        for (i in 0 until m) {
            var sum = 0f
            for (p in 0 until k) {
                val xValue = if (transX) x[p * m + i] else x[i * k + p]
                sum += xValue * y[p]
            }
            result[i] = sum
        }
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        val result = DataBufferGenerator.create(b * m * n)
        val strideX = m * k
        val strideY = k * n
        val stride = m * n
        for (batchIndex in 0 until b) {
            val offsetX = batchIndex * strideX
            val offsetY = batchIndex * strideY
            val offset = batchIndex * stride

            for (i in 0 until m) {
                for (j in 0 until n) {
                    var sum = 0f
                    for (p in 0 until k) {
                        val xValue = if (transX) x[offsetX + p * m + i] else x[offsetX + i * k + p]
                        val yValue = if (transY) y[offsetY + j * k + p] else y[offsetY + p * n + j]
                        sum += xValue * yValue
                    }
                    val index = offset + i * n + j
                    result[index] = sum
                }
            }
        }
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer = x.map { kotlin.math.exp(it) }

    override fun ln(x: DataBuffer, e: Float): DataBuffer = x.map { kotlin.math.ln(it + e) }

    override fun sigmoid(x: DataBuffer): DataBuffer = x.map { 1 / (1 + kotlin.math.exp(-it)) }

    override fun pow(x: DataBuffer, n: Int): DataBuffer = x.map { it.pow(n) }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer = x.map { kotlin.math.sqrt(it + e) }

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        val value = FloatArray(size) { random.nextDouble(from.toDouble(), until.toDouble()).toFloat() }
        return Default(value)
    }

    override fun average(x: DataBuffer): DataBuffer {
        val sum = sum(x = x)
        return div(sum, x.size.toFloat())
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val sum = sum(x = x, xi = xi, xj = xj, axis = axis)
        return div(
            x = sum,
            y = when (axis) {
                0 -> xi.toFloat()
                else -> xj.toFloat()
            },
        )
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val sum = sum(x = x, xi = xi, xj = xj, xk = xk, axis = axis)
        return div(
            x = sum,
            y = when (axis) {
                0 -> xi.toFloat()
                1 -> xj.toFloat()
                else -> xk.toFloat()
            },
        )
    }

    override fun max(x: DataBuffer): DataBuffer = x.reduce { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> maxOf(acc, i) }

    override fun min(x: DataBuffer): DataBuffer = x.reduce { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> minOf(acc, i) }

    override fun sum(x: DataBuffer): DataBuffer = x.reduce { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer = x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> acc + i }

    override fun maxIndex(x: DataBuffer): DataBuffer {
        val x = x.toFloatArray()
        var index = 0
        var max = x[0]
        for (i in 1 until x.size) {
            if (max < x[i]) {
                index = i
                max = x[i]
            }
        }
        return Default(1).apply { this[0] = index.toFloat() }
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer = x.reduceIndex(xi, xj, axis) { maxIndex(it)[0] }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer = x.reduceIndex(xi, xj, xk, axis) { maxIndex(it)[0] }

    override fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer {
        val x = x.toFloatArray()
        val total = Array(x.size) { x[it] to it }
            .apply { sortWith(compareByDescending { (value, _) -> value }) }
        val index = total
            .sliceArray(0 until minOf(k, x.size))
            .randomIndex(random)
        return Default(1).apply { this[0] = index.toFloat() }
    }
    override fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer = x.reduceIndex(xi, xj, axis) { topK(it, k, random)[0] }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer = x.reduceIndex(xi, xj, xk, axis) { topK(it, k, random)[0] }

    override fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer {
        val x = x.toFloatArray()
        val total = Array(x.size) { x[it] to it }
            .apply { sortWith(compareByDescending { (value, _) -> value }) }
        var sum = 0.0f
        for (i in 0 until total.size) {
            sum += total[i].first
            if (sum > p) {
                val index = total.sliceArray(0..i).randomIndex(random)
                return Default(1).apply { this[0] = index.toFloat() }
            }
        }
        val index = total.randomIndex(random)
        return Default(1).apply { this[0] = index.toFloat() }
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer = x.reduceIndex(xi, xj, axis) { topP(it, p, random)[0] }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer = x.reduceIndex(xi, xj, xk, axis) { topP(it, p, random)[0] }

    private fun Array<Pair<Float, Int>>.randomIndex(random: Random): Int {
        val sum = sumOf { (value, _) -> value.toDouble() }
        if (sum == 0.0) return random(random).second

        var p = random.nextDouble(from = 0.0, until = sum)
        for ((value, index) in this) {
            p -= value
            if (p <= 0) return index
        }
        return last().second
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in 0 until xi) {
            for (j in 0 until xj) {
                result[j * xi + i] = x[i * xj + j]
            }
        }
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val oldShape = listOf(xi, xj, xk)
        val newShape = listOf(oldShape[axisI], oldShape[axisJ], oldShape[axisK])
        val result = DataBufferGenerator.create(x.size)
        for (ni in 0 until newShape[0]) {
            val nii = ni * newShape[1]
            for (nj in 0 until newShape[1]) {
                val nji = (nii + nj) * newShape[2]
                for (nk in 0 until newShape[2]) {
                    val newIndex = nji + nk

                    val (oi, oj, ok) = IntArray(3).apply {
                        this[axisI] = ni
                        this[axisJ] = nj
                        this[axisK] = nk
                    }
                    val oldIndex = (oi * xj + oj) * xk + ok

                    result[newIndex] = x[oldIndex]
                }
            }
        }
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        val oldShape = listOf(xi, xj, xk, xl)
        val newShape = listOf(oldShape[axisI], oldShape[axisJ], oldShape[axisK], oldShape[axisL])
        val result = DataBufferGenerator.create(x.size)
        for (ni in 0 until newShape[0]) {
            val nii = ni * newShape[1]
            for (nj in 0 until newShape[1]) {
                val nji = (nii + nj) * newShape[2]
                for (nk in 0 until newShape[2]) {
                    val nki = (nji + nk) * newShape[3]
                    for (nl in 0 until newShape[3]) {
                        val newIndex = nki + nl

                        val (oi, oj, ok, ol) = IntArray(4).apply {
                            this[axisI] = ni
                            this[axisJ] = nj
                            this[axisK] = nk
                            this[axisL] = nl
                        }
                        val oldIndex = ((oi * xj + oj) * xk + ok) * xl + ol

                        result[newIndex] = x[oldIndex]
                    }
                }
            }
        }
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, xm: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, axisM: Int): DataBuffer {
        val oldShape = listOf(xi, xj, xk, xl, xm)
        val newShape = listOf(oldShape[axisI], oldShape[axisJ], oldShape[axisK], oldShape[axisL], oldShape[axisM])
        val result = DataBufferGenerator.create(x.size)
        for (ni in 0 until newShape[0]) {
            val nii = ni * newShape[1]
            for (nj in 0 until newShape[1]) {
                val nji = (nii + nj) * newShape[2]
                for (nk in 0 until newShape[2]) {
                    val nki = (nji + nk) * newShape[3]
                    for (nl in 0 until newShape[3]) {
                        val nli = (nki + nl) * newShape[4]
                        for (nm in 0 until newShape[4]) {
                            val newIndex = nli + nm

                            val (oi, oj, ok, ol, om) = IntArray(5).apply {
                                this[axisI] = ni
                                this[axisJ] = nj
                                this[axisK] = nk
                                this[axisL] = nl
                                this[axisM] = nm
                            }
                            val oldIndex = (((oi * xj + oj) * xk + ok) * xl + ol) * xm + om

                            result[newIndex] = x[oldIndex]
                        }
                    }
                }
            }
        }
        return result
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer {
        val result = DataBufferGenerator.create(indices.size)
        var resultIndex = 0
        for (i in indices) {
            result[resultIndex++] = x[i]
        }
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer = when (axis) {
        0 -> {
            val count = min(xi, indices.size)
            val result = DataBufferGenerator.create(count * xj)
            var resultIndex = 0
            for (i in indices) {
                val xOffset = i * xj
                for (j in 0 until xj) {
                    result[resultIndex++] = x[xOffset + j]
                }
            }
            result
        }

        else -> {
            val count = min(xj, indices.size)
            val result = DataBufferGenerator.create(xi * count)
            for (i in 0 until xi) {
                val xOffset = i * xj
                var resultIndex = i * count
                for (j in indices) {
                    result[resultIndex++] = x[xOffset + j]
                }
            }
            result
        }
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer = when (axis) {
        0 -> {
            val count = min(xi, indices.size)
            val result = DataBufferGenerator.create(count * xj * xk)
            var resultIndex = 0
            for (i in indices) {
                val xOffset = i * xj * xk
                for (jk in 0 until xj * xk) {
                    result[resultIndex++] = x[xOffset + jk]
                }
            }
            result
        }

        1 -> {
            val count = min(xj, indices.size)
            val result = DataBufferGenerator.create(xi * count * xk)
            for (i in 0 until xi) {
                val xii = i * xj * xk
                var resultIndex = i * count * xk
                for (j in indices) {
                    val xOffset = xii + j * xk
                    for (k in 0 until xk) {
                        result[resultIndex++] = x[xOffset + k]
                    }
                }
            }
            result
        }

        else -> {
            val count = min(xk, indices.size)
            val result = DataBufferGenerator.create(xi * xj * count)
            for (ij in 0 until xi * xj) {
                val xOffset = ij * xk
                var resultIndex = ij * count
                for (k in indices) {
                    result[resultIndex++] = x[xOffset + k]
                }
            }
            result
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        var xIndex = 0
        for (i in indices) {
            y[i] = x[xIndex++]
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression) {
        when (axis) {
            0 -> {
                var xIndex = 0
                for (i in indices) {
                    val yOffset = i * yj
                    for (j in 0 until yj) {
                        y[yOffset + j] = x[xIndex++]
                    }
                }
            }

            else -> {
                val count = min(yj, indices.size)
                for (i in 0 until yi) {
                    val yOffset = i * yj
                    var xIndex = i * count
                    for (j in indices) {
                        y[yOffset + j] = x[xIndex++]
                    }
                }
            }
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        when (axis) {
            0 -> {
                var xIndex = 0
                for (i in indices) {
                    val yOffset = i * yj * yk
                    for (jk in 0 until yj * yk) {
                        y[yOffset + jk] = x[xIndex++]
                    }
                }
            }

            1 -> {
                val count = min(yj, indices.size)
                for (i in 0 until yi) {
                    val yii = i * yj * yk
                    var xIndex = i * count * yk
                    for (j in indices) {
                        val yOffset = yii + j * yk
                        for (k in 0 until yk) {
                            y[yOffset + k] = x[xIndex++]
                        }
                    }
                }
            }

            else -> {
                val count = min(yk, indices.size)
                for (ij in 0 until yi * yj) {
                    val yOffset = ij * yk
                    var xIndex = ij * count
                    for (k in indices) {
                        y[yOffset + k] = x[xIndex++]
                    }
                }
            }
        }
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        val n = x.size
        val result = DataBufferGenerator.create(i * n * k)
        for (ri in 0 until i) {
            for (rj in 0 until n) {
                val index = x[rj].toInt()
                val yOffset = (ri * j + index) * k
                val resultOffset = (ri * n + rj) * k
                for (rk in 0 until k) {
                    val yIndex = yOffset + rk
                    val resultIndex = resultOffset + rk
                    result[resultIndex] = y[yIndex]
                }
            }
        }
        return result
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        val n = y.size / b
        val result = DataBufferGenerator.create(i * j * k)
        for (xb in 0 until b) {
            for (xi in 0 until i) {
                for (xj in 0 until n) {
                    val index = y[xb * n + xj].toInt()
                    val xOffset = ((xb * i + xi) * n + xj) * k
                    val resultOffset = (xi * j + index) * k
                    for (xk in 0 until k) {
                        result[resultOffset + xk] += x[xOffset + xk]
                    }
                }
            }
        }
        return result
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in result.indices) result[i] = if (x[i] > y) 1f else 0f
        return result
    }

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in result.indices) result[i] = if (x[i] > y[i]) 1f else 0f
        return result
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in result.indices) result[i] = if (x[i] < y) 1f else 0f
        return result
    }

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in result.indices) result[i] = if (x[i] < y[i]) 1f else 0f
        return result
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        val tolerance = absoluteTolerance + relativeTolerance * abs(y)
        for (i in result.indices) result[i] = if (abs(x[i] - y) <= tolerance) 1f else 0f
        return result
    }

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in result.indices) {
            val tolerance = absoluteTolerance + relativeTolerance * abs(y[i])
            result[i] = if (abs(x[i] - y[i]) <= tolerance) 1f else 0f
        }
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer {
        val result = DataBufferGenerator.create(condition.size)
        for (i in result.indices) result[i] = if (condition[i] > 0f) x else y
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        val result = DataBufferGenerator.create(condition.size)
        for (i in result.indices) result[i] = if (condition[i] > 0f) x else y[i]
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        val result = DataBufferGenerator.create(condition.size)
        for (i in result.indices) result[i] = if (condition[i] > 0f) x[i] else y
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = DataBufferGenerator.create(condition.size)
        for (i in result.indices) result[i] = if (condition[i] > 0f) x[i] else y[i]
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj - windowSize + padding * 2) / stride + 1
        val result = DataBufferGenerator.create(b * xi * oj * window)
        for (xb in 0 until b) {
            for (i in 0 until xi) {
                for (j in 0 until oj) {
                    for (w in 0 until window) {
                        val index = j * stride + w * dilation - padding
                        if (index in 0 until xj) {
                            val resultIndex = ((xb * xi + i) * oj + j) * window + w
                            val xIndex = (xb * xi + i) * xj + index
                            result[resultIndex] = x[xIndex]
                        }
                    }
                }
            }
        }
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj - windowSize + padding * 2) / stride + 1
        val ok = (xk - windowSize + padding * 2) / stride + 1
        val ww = window * window
        val result = DataBufferGenerator.create(b * xi * oj * ok * ww)
        for (xb in 0 until b) {
            for (i in 0 until xi) {
                for (ox in 0 until oj) {
                    for (oy in 0 until ok) {
                        for (wx in 0 until window) {
                            for (wy in 0 until window) {
                                val inputI = ox * stride + wy * dilation - padding
                                val inputJ = oy * stride + wx * dilation - padding
                                if (inputI in 0 until xj && inputJ in 0 until xk) {
                                    val resultIndex = (((xb * xi + i) * oj + ox) * ok + oy) * ww + wy * window + wx
                                    val xIndex = ((xb * xi + i) * xj + inputI) * xk + inputJ
                                    result[resultIndex] = x[xIndex]
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (xk - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val result = DataBufferGenerator.create(b * xi * oj)
        for (xb in 0 until b) {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val index = j * stride + k * dilation - padding
                        if (index in 0 until oj) {
                            val xIndex = ((xb * xi + i) * xj + j) * xk + k
                            val resultIndex = (xb * xi + i) * oj + index
                            result[resultIndex] += x[xIndex]
                        }
                    }
                }
            }
        }
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val window = kotlin.math.sqrt(xl.toDouble()).toInt()
        val windowSize = (window - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val ok = windowSize + (xk - 1) * stride - padding * 2
        val result = DataBufferGenerator.create(b * xi * oj * ok)
        for (xb in 0 until b) {
            for (i in 0 until xi) {
                for (ox in 0 until xj) {
                    for (oy in 0 until xk) {
                        for (wx in 0 until window) {
                            for (wy in 0 until window) {
                                val inputI = ox * stride + wy * dilation - padding
                                val inputJ = oy * stride + wx * dilation - padding
                                if (inputI in 0 until oj && inputJ in 0 until ok) {
                                    val xIndex = (((xb * xi + i) * xj + ox) * xk + oy) * xl + wy * window + wx
                                    val resultIndex = ((xb * xi + i) * oj + inputI) * ok + inputJ
                                    result[resultIndex] += x[xIndex]
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = DataBufferGenerator.create(x.size)
        for (i in 0 until xi) {
            for (j in 0 until xj) {
                for (k in 0 until xk) {
                    val si = if (axis == 0) xi - 1 - i else i
                    val sj = if (axis == 1) xj - 1 - j else j
                    val sk = if (axis == 2) xk - 1 - k else k
                    result[i * xj * xk + j * xk + k] = x[si * xj * xk + sj * xk + sk]
                }
            }
        }
        return result
    }

    override fun flush() {}
    override fun sync() {}
}

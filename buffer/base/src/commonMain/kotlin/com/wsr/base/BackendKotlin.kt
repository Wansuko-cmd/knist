package com.wsr.base

import kotlin.math.pow

object BackendKotlin : IBackend {
    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer = y.map { x + it }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer = x.map { it + y }
    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer = x.zipWith(other = y) { a, b -> a + b }
    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a + b }
    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a + b }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a + b }
    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a + b }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a + b }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        yl: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            other = y,
            axis = axis,
        ) { a, b -> a + b }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a - b }
    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a - b }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a - b }
    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a - b }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a - b }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        yl: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            other = y,
            axis = axis,
        ) { a, b -> a - b }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a * b }
    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a * b }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a * b }
    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a * b }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a * b }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        yl: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            other = y,
            axis = axis,
        ) { a, b -> a * b }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, axis = axis) { a, b -> a / b }
    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer =
        x.zipWith(other = y, yi = yi, yj = yj, yk = yk, axis = axis) { a, b -> a / b }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, other = y, axis = axis) { a, b -> a / b }
    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(xi = xi, xj = xj, xk = xk, other = y, axis = axis) { a, b -> a / b }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
        xi = xi,
        xj = xj,
        xk = xk,
        other = y,
        yi = yi,
        yj = yj,
        axis1 = axis1,
        axis2 = axis2,
    ) { a, b -> a / b }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        yl: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer =
        x.zipWith(
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            other = y,
            axis = axis,
        ) { a, b -> a / b }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer = x.zipWith(
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

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
        axis3: Int,
    ): DataBuffer = x.zipWith(
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
        val result = DataBuffer.create(b)
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
        val result = DataBuffer.create(n)
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
        val result = DataBuffer.create(m)
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

    override fun matMul(
        x: DataBuffer,
        transX: Boolean,
        y: DataBuffer,
        transY: Boolean,
        m: Int,
        n: Int,
        k: Int,
        b: Int,
    ): DataBuffer {
        val result = DataBuffer.create(b * m * n)
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

    override fun pow(x: DataBuffer, n: Int): DataBuffer = x.map { it.pow(n) }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer = x.map { kotlin.math.sqrt(it + e) }

    override fun max(x: DataBuffer): Float = x.reduce { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xb: Int): DataBuffer = x.reduce(xb = xb) { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> maxOf(acc, i) }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, xl = xl, axis = axis) { acc, i -> maxOf(acc, i) }

    override fun min(x: DataBuffer): Float = x.reduce { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xb: Int): DataBuffer = x.reduce(xb = xb) { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> minOf(acc, i) }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, xl = xl, axis = axis) { acc, i -> minOf(acc, i) }

    override fun sum(x: DataBuffer): Float = x.reduce { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xb: Int): DataBuffer = x.reduce(xb = xb) { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, axis = axis) { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, axis = axis) { acc, i -> acc + i }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer =
        x.reduce(xi = xi, xj = xj, xk = xk, xl = xl, axis = axis) { acc, i -> acc + i }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = DataBuffer.create(x.size)
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
        val result = DataBuffer.create(x.size)
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

    override fun transpose(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        axisI: Int,
        axisJ: Int,
        axisK: Int,
        axisL: Int,
    ): DataBuffer {
        val oldShape = listOf(xi, xj, xk, xl)
        val newShape = listOf(oldShape[axisI], oldShape[axisJ], oldShape[axisK], oldShape[axisL])
        val result = DataBuffer.create(x.size)
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
}

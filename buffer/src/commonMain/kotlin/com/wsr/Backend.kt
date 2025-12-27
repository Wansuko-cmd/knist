package com.wsr

import com.wsr.base.DataBuffer
import com.wsr.base.IBackend
import com.wsr.cpu.cpu

object Backend : IBackend {
    private var instance: IBackend = cpu

    fun set(backend: IBackend) {
        instance = backend
    }

    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        return instance.plus(x, y)
    }

    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        return instance.plus(x, y)
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.plus(x, y)
    }

    override fun plus(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.plus(x, y, yi, yj, axis)
    }

    override fun plus(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.plus(x, y, yi, yj, yk, axis)
    }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.plus(x, xi, xj, y, axis)
    }

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
    ): DataBuffer {
        return instance.plus(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, y, axis)
    }

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
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, xl, y, axis)
    }

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
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.plus(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        return instance.minus(x, y)
    }

    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        return instance.minus(x, y)
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.minus(x, y)
    }

    override fun minus(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.minus(x, y, yi, yj, axis)
    }

    override fun minus(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.minus(x, y, yi, yj, yk, axis)
    }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.minus(x, xi, xj, y, axis)
    }

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
    ): DataBuffer {
        return instance.minus(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, y, axis)
    }

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
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, xl, y, axis)
    }

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
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.minus(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun times(x: Float, y: DataBuffer): DataBuffer {
        return instance.times(x, y)
    }

    override fun times(x: DataBuffer, y: Float): DataBuffer {
        return instance.times(x, y)
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.times(x, y)
    }

    override fun times(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.times(x, y, yi, yj, axis)
    }

    override fun times(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.times(x, y, yi, yj, yk, axis)
    }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.times(x, xi, xj, y, axis)
    }

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
    ): DataBuffer {
        return instance.times(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, y, axis)
    }

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
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, xl, y, axis)
    }

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
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.times(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun div(x: Float, y: DataBuffer): DataBuffer {
        return instance.div(x, y)
    }

    override fun div(x: DataBuffer, y: Float): DataBuffer {
        return instance.div(x, y)
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.div(x, y)
    }

    override fun div(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.div(x, y, yi, yj, axis)
    }

    override fun div(
        x: DataBuffer,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.div(x, y, yi, yj, yk, axis)
    }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.div(x, xi, xj, y, axis)
    }

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
    ): DataBuffer {
        return instance.div(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, y, axis)
    }

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
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        y: DataBuffer,
        axis: Int,
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, xl, y, axis)
    }

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
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

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
    ): DataBuffer {
        return instance.div(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        return instance.inner(x, y, b)
    }

    override fun matMul(
        x: DataBuffer,
        y: DataBuffer,
        transY: Boolean,
        n: Int,
        k: Int,
    ): DataBuffer {
        return instance.matMul(x, y, transY, n, k)
    }

    override fun matMul(
        x: DataBuffer,
        transX: Boolean,
        y: DataBuffer,
        m: Int,
        k: Int,
    ): DataBuffer {
        return instance.matMul(x, transX, y, m, k)
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
        return instance.matMul(x, transX, y, transY, m, n, k, b)
    }

    override fun exp(x: DataBuffer): DataBuffer {
        return instance.exp(x)
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        return instance.ln(x, e)
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        return instance.pow(x, n)
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        return instance.sqrt(x, e)
    }

    override fun max(x: DataBuffer): Float {
        return instance.max(x)
    }

    override fun max(x: DataBuffer, xb: Int): DataBuffer {
        return instance.max(x, xb)
    }

    override fun max(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.max(x, xi, xj, axis)
    }

    override fun max(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.max(x, xi, xj, xk, axis)
    }

    override fun max(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        axis: Int,
    ): DataBuffer {
        return instance.max(x, xi, xj, xk, xl, axis)
    }

    override fun min(x: DataBuffer): Float {
        return instance.min(x)
    }

    override fun min(x: DataBuffer, xb: Int): DataBuffer {
        return instance.min(x, xb)
    }

    override fun min(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.min(x, xi, xj, axis)
    }

    override fun min(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.min(x, xi, xj, xk, axis)
    }

    override fun min(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        axis: Int,
    ): DataBuffer {
        return instance.min(x, xi, xj, xk, xl, axis)
    }

    override fun sum(x: DataBuffer): Float {
        return instance.sum(x)
    }

    override fun sum(x: DataBuffer, xb: Int): DataBuffer {
        return instance.sum(x, xb)
    }

    override fun sum(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        axis: Int,
    ): DataBuffer {
        return instance.sum(x, xi, xj, axis)
    }

    override fun sum(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        axis: Int,
    ): DataBuffer {
        return instance.sum(x, xi, xj, xk, axis)
    }

    override fun sum(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        xl: Int,
        axis: Int,
    ): DataBuffer {
        return instance.sum(x, xi, xj, xk, xl, axis)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        return instance.transpose(x, xi, xj)
    }

    override fun transpose(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        axisI: Int,
        axisJ: Int,
        axisK: Int,
    ): DataBuffer {
        return instance.transpose(x, xi, xj, xk, axisI, axisJ, axisK)
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
        return instance.transpose(x, xi, xj, xk, xl, axisI, axisJ, axisK, axisL)
    }
}

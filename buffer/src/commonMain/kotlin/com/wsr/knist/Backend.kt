package com.wsr.knist

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.KotlinBackend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.DataBufferGenerator
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.cpu.loadCPUBackend
import kotlin.random.Random

object Backend : IBackend {
    private var instance: IBackend = loadCPUBackend(fallback = KotlinBackend)

    init {
        DataBufferGenerator.set(instance.generator)
    }

    fun set(backend: IBackend) {
        instance = backend
        DataBufferGenerator.set(backend.generator)
    }

    override val generator: IDataBufferGenerator get() = instance.generator

    override fun plus(x: Float, y: DataBuffer): DataBuffer = instance.plus(x, y)

    override fun plus(x: DataBuffer, y: Float): DataBuffer = instance.plus(x, y)

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.plus(x, y)
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj)
        check(axis == 0 || axis == 1)
        return instance.plus(x, y, yi, yj, axis)
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj * yk)
        check(axis in 0..2)
        return instance.plus(x, y, yi, yj, yk, axis)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.plus(x, xi, xj, y, axis)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.plus(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.plus(x, xi, xj, xk, y, axis)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.plus(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj * yk * yl)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.plus(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(axis in 0..3)
        return instance.plus(x, xi, xj, xk, xl, y, axis)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 3)
        return instance.plus(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.plus(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun minus(x: Float, y: DataBuffer): DataBuffer = instance.minus(x, y)

    override fun minus(x: DataBuffer, y: Float): DataBuffer = instance.minus(x, y)

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.minus(x, y)
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj)
        check(axis == 0 || axis == 1)
        return instance.minus(x, y, yi, yj, axis)
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj * yk)
        check(axis in 0..2)
        return instance.minus(x, y, yi, yj, yk, axis)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.minus(x, xi, xj, y, axis)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.minus(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.minus(x, xi, xj, xk, y, axis)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.minus(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj * yk * yl)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.minus(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(axis in 0..3)
        return instance.minus(x, xi, xj, xk, xl, y, axis)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 3)
        return instance.minus(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.minus(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun times(x: Float, y: DataBuffer): DataBuffer = instance.times(x, y)

    override fun times(x: DataBuffer, y: Float): DataBuffer = instance.times(x, y)

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.times(x, y)
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj)
        check(axis == 0 || axis == 1)
        return instance.times(x, y, yi, yj, axis)
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj * yk)
        check(axis in 0..2)
        return instance.times(x, y, yi, yj, yk, axis)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.times(x, xi, xj, y, axis)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.times(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.times(x, xi, xj, xk, y, axis)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.times(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj * yk * yl)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.times(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(axis in 0..3)
        return instance.times(x, xi, xj, xk, xl, y, axis)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 3)
        return instance.times(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.times(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun div(x: Float, y: DataBuffer): DataBuffer = instance.div(x, y)

    override fun div(x: DataBuffer, y: Float): DataBuffer = instance.div(x, y)

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.div(x, y)
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj)
        check(axis == 0 || axis == 1)
        return instance.div(x, y, yi, yj, axis)
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        check(y.size == yi * yj * yk)
        check(axis in 0..2)
        return instance.div(x, y, yi, yj, yk, axis)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.div(x, xi, xj, y, axis)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.div(x, xi, xj, y, yi, yj, yk, axis1, axis2)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.div(x, xi, xj, xk, y, axis)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 2)
        return instance.div(x, xi, xj, xk, y, yi, yj, axis1, axis2)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(y.size == yi * yj * yk * yl)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.div(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(axis in 0..3)
        return instance.div(x, xi, xj, xk, xl, y, axis)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj)
        check(0 <= axis1 && axis1 < axis2 && axis2 <= 3)
        return instance.div(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2)
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(y.size == yi * yj * yk)
        check(0 <= axis1 && axis1 < axis2 && axis2 < axis3 && axis3 <= 3)
        return instance.div(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3)
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        check(b > 0)
        check(x.size % b == 0)
        check(x.size == y.size)
        return instance.inner(x, y, b)
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        check(x.size == k)
        check(y.size == n * k)
        return instance.matMul(x, y, transY, n, k)
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        check(y.size == k)
        check(x.size == m * k)
        return instance.matMul(x, transX, y, m, k)
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        check(b > 0)
        check(x.size == b * m * k)
        check(y.size == b * k * n)
        return instance.matMul(x, transX, y, transY, m, n, k, b)
    }

    override fun exp(x: DataBuffer): DataBuffer = instance.exp(x)

    override fun ln(x: DataBuffer, e: Float): DataBuffer = instance.ln(x, e)

    override fun sigmoid(x: DataBuffer): DataBuffer = instance.sigmoid(x)

    override fun pow(x: DataBuffer, n: Int): DataBuffer = instance.pow(x, n)

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer = instance.sqrt(x, e)

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        check(from < until)
        return instance.random(size, from, until, random)
    }

    override fun average(x: DataBuffer): DataBuffer = instance.average(x)

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.average(x, xi, xj, axis)
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.average(x, xi, xj, xk, axis)
    }

    override fun max(x: DataBuffer): DataBuffer = instance.max(x)

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.max(x, xi, xj, axis)
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.max(x, xi, xj, xk, axis)
    }

    override fun min(x: DataBuffer): DataBuffer = instance.min(x)

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.min(x, xi, xj, axis)
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.min(x, xi, xj, xk, axis)
    }

    override fun sum(x: DataBuffer): DataBuffer = instance.sum(x)

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.sum(x, xi, xj, axis)
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.sum(x, xi, xj, xk, axis)
    }

    override fun maxIndex(x: DataBuffer): DataBuffer = instance.maxIndex(x)

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.maxIndex(x, xi, xj, axis)
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.maxIndex(x, xi, xj, xk, axis)
    }

    override fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer {
        check(k > 0)
        return instance.topK(x, k, random)
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        check(k > 0)
        return instance.topK(x, xi, xj, k, axis, random)
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        check(k > 0)
        return instance.topK(x, xi, xj, xk, k, axis, random)
    }

    override fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer = instance.topP(x, p, random)

    override fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.topP(x, xi, xj, p, axis, random)
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.topP(x, xi, xj, xk, p, axis, random)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        check(x.size == xi * xj)
        return instance.transpose(x, xi, xj)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axisI in 0..2 && axisJ in 0..2 && axisK in 0..2)
        check(axisI != axisJ && axisJ != axisK && axisK != axisI)
        return instance.transpose(x, xi, xj, xk, axisI, axisJ, axisK)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl)
        check(axisI in 0..3 && axisJ in 0..3 && axisK in 0..3 && axisL in 0..3)
        check(axisI != axisJ && axisI != axisK && axisI != axisL)
        check(axisJ != axisK && axisJ != axisL)
        check(axisK != axisL)
        return instance.transpose(x, xi, xj, xk, xl, axisI, axisJ, axisK, axisL)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, xm: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, axisM: Int): DataBuffer {
        check(x.size == xi * xj * xk * xl * xm)
        check(axisI in 0..4 && axisJ in 0..4 && axisK in 0..4 && axisL in 0..4 && axisM in 0..4)
        check(axisI != axisJ && axisI != axisK && axisI != axisL && axisI != axisM)
        check(axisJ != axisK && axisJ != axisL && axisJ != axisM)
        check(axisK != axisL && axisK != axisM)
        check(axisL != axisM)
        return instance.transpose(x, xi, xj, xk, xl, xm, axisI, axisJ, axisK, axisL, axisM)
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer = instance.slice(x, indices)

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer {
        check(x.size == xi * xj)
        check(axis == 0 || axis == 1)
        return instance.slice(x, xi, xj, axis, indices)
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.slice(x, xi, xj, xk, axis, indices)
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        instance.copyInto(x, y, indices)
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression) {
        check(y.size == yi * yj)
        check(axis == 0 || axis == 1)
        instance.copyInto(x, y, yi, yj, axis, indices)
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        check(y.size == yi * yj * yk)
        check(axis in 0..2)
        instance.copyInto(x, y, yi, yj, yk, axis, indices)
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        check(y.size == i * j * k)
        return instance.gather(x, y, i, j, k)
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        check(b > 0)
        check(y.size % b == 0)
        check(x.size == b * i * (y.size / b) * k)
        return instance.scatterAdd(x, y, i, j, k, b)
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer = instance.greaterThan(x, y)

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.greaterThan(x, y)
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer = instance.lessThan(x, y)

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        check(x.size == y.size)
        return instance.lessThan(x, y)
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer = instance.equals(x, y, absoluteTolerance, relativeTolerance)

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        check(x.size == y.size)
        return instance.equals(x, y, absoluteTolerance, relativeTolerance)
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer = instance.where(condition, x, y)

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        check(condition.size == y.size)
        return instance.where(condition, x, y)
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        check(condition.size == x.size)
        return instance.where(condition, x, y)
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        check(condition.size == x.size && condition.size == y.size)
        return instance.where(condition, x, y)
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        check(x.size == b * xi * xj)
        check(window > 0)
        check(stride > 0)
        check(dilation > 0)
        check(padding >= 0)
        return instance.unfold(x, xi, xj, b, window, stride, dilation, padding)
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        check(x.size == b * xi * xj * xk)
        check(window > 0)
        check(stride > 0)
        check(dilation > 0)
        check(padding >= 0)
        return instance.unfold(x, xi, xj, xk, b, window, stride, dilation, padding)
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        check(x.size == b * xi * xj * xk)
        check(stride > 0)
        check(dilation > 0)
        check(padding >= 0)
        return instance.fold(x, xi, xj, xk, b, stride, dilation, padding)
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        check(x.size == b * xi * xj * xk * xl)
        check(stride > 0)
        check(dilation > 0)
        check(padding >= 0)
        return instance.fold(x, xi, xj, xk, xl, b, stride, dilation, padding)
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        check(x.size == xi * xj * xk)
        check(axis in 0..2)
        return instance.flip(x, xi, xj, xk, axis)
    }

    override fun flush() = instance.flush()
    override fun sync() = instance.sync()
}

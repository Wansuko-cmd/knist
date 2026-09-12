package com.wsr.knist.base

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import kotlin.random.Random

interface IBackend {
    val generator: IDataBufferGenerator

    // 0次元
    fun plus(x: Float, y: DataBuffer): DataBuffer

    // 1次元
    fun plus(x: DataBuffer, y: Float): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer

    // 2次元
    fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer

    // 3次元
    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer

    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 4次元
    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer

    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 0次元
    fun minus(x: Float, y: DataBuffer): DataBuffer

    // 1次元
    fun minus(x: DataBuffer, y: Float): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer

    // 2次元
    fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer

    // 3次元
    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer

    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 4次元
    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer

    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 0次元
    fun times(x: Float, y: DataBuffer): DataBuffer

    // 1次元
    fun times(x: DataBuffer, y: Float): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer

    // 2次元
    fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer

    // 3次元
    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer

    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 4次元
    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer

    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 0次元
    fun div(x: Float, y: DataBuffer): DataBuffer

    // 1次元
    fun div(x: DataBuffer, y: Float): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer

    // 2次元
    fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer

    // 3次元
    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer

    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    // 4次元
    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer

    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer

    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer

    fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer

    fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer

    fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer

    fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer

    fun exp(x: DataBuffer): DataBuffer
    fun ln(x: DataBuffer, e: Float): DataBuffer
    fun sigmoid(x: DataBuffer): DataBuffer
    fun pow(x: DataBuffer, n: Int): DataBuffer
    fun sqrt(x: DataBuffer, e: Float): DataBuffer

    fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer

    fun average(x: DataBuffer): DataBuffer
    fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun max(x: DataBuffer): DataBuffer
    fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun min(x: DataBuffer): DataBuffer
    fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun sum(x: DataBuffer): DataBuffer
    fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun maxIndex(x: DataBuffer): DataBuffer
    fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer
    fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer
    fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer

    fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer
    fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer
    fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer

    fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer
    fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer
    fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer

    fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, xm: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, axisM: Int): DataBuffer

    fun slice(x: DataBuffer, indices: IntProgression): DataBuffer

    fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer

    fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer

    fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression)

    fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression)

    fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression)

    fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer

    fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer

    fun greaterThan(x: DataBuffer, y: Float): DataBuffer
    fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer

    fun lessThan(x: DataBuffer, y: Float): DataBuffer
    fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer

    fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer
    fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer

    fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer
    fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer
    fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer
    fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer

    fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer
    fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer

    fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer
    fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer

    fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun flush()
    fun sync()
}

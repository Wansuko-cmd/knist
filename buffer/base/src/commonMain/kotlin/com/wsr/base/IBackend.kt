package com.wsr.base

interface IBackend {
    fun plus(x: Float, y: DataBuffer): DataBuffer
    fun plus(x: DataBuffer, y: Float): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer
    fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer
    fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer
    fun plus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer

    fun minus(x: Float, y: DataBuffer): DataBuffer
    fun minus(x: DataBuffer, y: Float): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer
    fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer
    fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer
    fun minus(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer

    fun times(x: Float, y: DataBuffer): DataBuffer
    fun times(x: DataBuffer, y: Float): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer
    fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer
    fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer
    fun times(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer

    fun div(x: Float, y: DataBuffer): DataBuffer
    fun div(x: DataBuffer, y: Float): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer
    fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer
    fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer
    fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        yk: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer
    fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer
    fun div(
        x: DataBuffer,
        xi: Int,
        xj: Int,
        xk: Int,
        y: DataBuffer,
        yi: Int,
        yj: Int,
        axis1: Int,
        axis2: Int,
    ): DataBuffer

    fun exp(x: DataBuffer): DataBuffer
    fun ln(x: DataBuffer, e: Float): DataBuffer
    fun pow(x: DataBuffer, n: Int): DataBuffer
    fun sqrt(x: DataBuffer, e: Float): DataBuffer

    fun max(x: DataBuffer): Float
    fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun min(x: DataBuffer): Float
    fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun sum(x: DataBuffer): Float
    fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer
    fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer

    fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer
    fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer
}

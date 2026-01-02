package com.wsr.cpu

import com.wsr.base.DataBuffer
import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "cpu"
private const val LIB_NAME = "cpu"

actual fun loadCPUBackend(): IBackend? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) CPUBackend() else null
}

class CPUBackend : IBackend by KotlinBackend {
    private val openBLAS = JOpenBLAS()
    private val operation = JOperation()
    private val math = JMath()
    private val collection = JCollection()
    private val transpose = JTranspose()

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.plusD0ToD1(x, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.plusD1ToD0(x.toCPUBuffer().byteBuffer, y, result.byteBuffer)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.plusD1ToD1(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.plusD1ToD2(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, yi, yj, axis, result.byteBuffer)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.plusD1ToD3(
            x.toCPUBuffer().byteBuffer,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis,
            result.byteBuffer,
        )
        return result
    }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.plusD2ToD1(x.toCPUBuffer().byteBuffer, xi, xj, y.toCPUBuffer().byteBuffer, axis, result.byteBuffer)
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.plusD2ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
    }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.plusD3ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.plusD3ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.plusD3ToD4(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.plusD4ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.plusD4ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.plusD4ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.minusD0ToD1(x, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.minusD1ToD0(x.toCPUBuffer().byteBuffer, y, result.byteBuffer)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.minusD1ToD1(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.minusD1ToD2(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, yi, yj, axis, result.byteBuffer)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.minusD1ToD3(
            x.toCPUBuffer().byteBuffer,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis,
            result.byteBuffer,
        )
        return result
    }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.minusD2ToD1(x.toCPUBuffer().byteBuffer, xi, xj, y.toCPUBuffer().byteBuffer, axis, result.byteBuffer)
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.minusD2ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
    }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.minusD3ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.minusD3ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.minusD3ToD4(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.minusD4ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.minusD4ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.minusD4ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.timesD0ToD1(x, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.timesD1ToD0(x.toCPUBuffer().byteBuffer, y, result.byteBuffer)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.timesD1ToD1(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.timesD1ToD2(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, yi, yj, axis, result.byteBuffer)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.timesD1ToD3(
            x.toCPUBuffer().byteBuffer,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis,
            result.byteBuffer,
        )
        return result
    }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.timesD2ToD1(x.toCPUBuffer().byteBuffer, xi, xj, y.toCPUBuffer().byteBuffer, axis, result.byteBuffer)
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.timesD2ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
    }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.timesD3ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.timesD3ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.timesD3ToD4(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.timesD4ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.timesD4ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.timesD4ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.divD0ToD1(x, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.divD1ToD0(x.toCPUBuffer().byteBuffer, y, result.byteBuffer)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.divD1ToD1(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.divD1ToD2(x.toCPUBuffer().byteBuffer, y.toCPUBuffer().byteBuffer, yi, yj, axis, result.byteBuffer)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(y.size)
        operation.divD1ToD3(
            x.toCPUBuffer().byteBuffer,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis,
            result.byteBuffer,
        )
        return result
    }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.divD2ToD1(x.toCPUBuffer().byteBuffer, xi, xj, y.toCPUBuffer().byteBuffer, axis, result.byteBuffer)
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.divD2ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
    }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.divD3ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.divD3ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(y.size)
        operation.divD3ToD4(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        operation.divD4ToD1(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            axis,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.divD4ToD2(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            axis1,
            axis2,
            result.byteBuffer,
        )
        return result
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
        val result = CPUBuffer.create(x.size)
        operation.divD4ToD3(
            x.toCPUBuffer().byteBuffer,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().byteBuffer,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.byteBuffer,
        )
        return result
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        openBLAS.sgemm(
            false,
            true,
            1,
            1,
            x.size,
            1f,
            x.toCPUBuffer().byteBuffer,
            x.size,
            y.toCPUBuffer().byteBuffer,
            x.size,
            0f,
            result.byteBuffer,
            1,
            1,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = CPUBuffer.create(m)
        openBLAS.sgemm(
            transX,
            false,
            m,
            1,
            k,
            1f,
            x.toCPUBuffer().byteBuffer,
            if (transX) m else k,
            y.toCPUBuffer().byteBuffer,
            1,
            0f,
            result.byteBuffer,
            1,
            1,
        )
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = CPUBuffer.create(n)
        openBLAS.sgemm(
            false,
            transY,
            1,
            n,
            k,
            1f,
            x.toCPUBuffer().byteBuffer,
            k,
            y.toCPUBuffer().byteBuffer,
            if (transY) k else n,
            0f,
            result.byteBuffer,
            n,
            1,
        )
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
        val result = CPUBuffer.create(b * m * n)
        openBLAS.sgemm(
            transX,
            transY,
            m,
            n,
            k,
            1f,
            x.toCPUBuffer().byteBuffer,
            if (transX) m else k,
            y.toCPUBuffer().byteBuffer,
            if (transY) k else n,
            0f,
            result.byteBuffer,
            n,
            b,
        )
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer {
        val result = CPUBuffer.create(x.size)
        math.exp(x.toCPUBuffer().byteBuffer, result.byteBuffer)
        return result
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        math.ln(x.toCPUBuffer().byteBuffer, e, result.byteBuffer)
        return result
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        math.pow(x.toCPUBuffer().byteBuffer, n, result.byteBuffer)
        return result
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUBuffer.create(x.size)
        math.sqrt(x.toCPUBuffer().byteBuffer, e, result.byteBuffer)
        return result
    }

    override fun max(x: DataBuffer): Float = collection.maxD1(x.toCPUBuffer().byteBuffer)

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        collection.maxD2(x.toCPUBuffer().byteBuffer, xi, xj, axis, result.byteBuffer)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        collection.maxD3(x.toCPUBuffer().byteBuffer, xi, xj, xk, axis, result.byteBuffer)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk * xl
                1 -> xi * xk * xl
                2 -> xi * xj * xl
                else -> xi * xj * xk
            },
        )
        collection.maxD4(x.toCPUBuffer().byteBuffer, xi, xj, xk, xl, axis, result.byteBuffer)
        return result
    }

    override fun min(x: DataBuffer): Float = collection.minD1(x.toCPUBuffer().byteBuffer)

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        collection.minD2(x.toCPUBuffer().byteBuffer, xi, xj, axis, result.byteBuffer)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        collection.minD3(x.toCPUBuffer().byteBuffer, xi, xj, xk, axis, result.byteBuffer)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk * xl
                1 -> xi * xk * xl
                2 -> xi * xj * xl
                else -> xi * xj * xk
            },
        )
        collection.minD4(x.toCPUBuffer().byteBuffer, xi, xj, xk, xl, axis, result.byteBuffer)
        return result
    }

    override fun sum(x: DataBuffer): Float = collection.sumD1(x.toCPUBuffer().byteBuffer)

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        collection.sumD2(x.toCPUBuffer().byteBuffer, xi, xj, axis, result.byteBuffer)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        collection.sumD3(x.toCPUBuffer().byteBuffer, xi, xj, xk, axis, result.byteBuffer)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axis: Int): DataBuffer {
        val result = CPUBuffer.create(
            size = when (axis) {
                0 -> xj * xk * xl
                1 -> xi * xk * xl
                2 -> xi * xj * xl
                else -> xi * xj * xk
            },
        )
        collection.sumD4(x.toCPUBuffer().byteBuffer, xi, xj, xk, xl, axis, result.byteBuffer)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        transpose.transposeD2(x.toCPUBuffer().byteBuffer, xi, xj, result.byteBuffer)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val result = CPUBuffer.create(x.size)
        transpose.transposeD3(x.toCPUBuffer().byteBuffer, xi, xj, xk, axisI, axisJ, axisK, result.byteBuffer)
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
        val result = CPUBuffer.create(x.size)
        transpose.transposeD4(x.toCPUBuffer().byteBuffer, xi, xj, xk, xl, axisI, axisJ, axisK, axisL, result.byteBuffer)
        return result
    }

    override fun create(size: Int): DataBuffer = CPUBuffer.create(size)

    override fun create(value: FloatArray): DataBuffer = CPUBuffer.create(value)
}

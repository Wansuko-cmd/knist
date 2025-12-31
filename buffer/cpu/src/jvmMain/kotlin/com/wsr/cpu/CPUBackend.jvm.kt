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
    private val transpose = JTranspose()

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

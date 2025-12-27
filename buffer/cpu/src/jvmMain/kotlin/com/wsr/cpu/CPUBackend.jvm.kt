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

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = FloatArray(x.size)
        openBLAS.sgemm(
            false,
            true,
            1,
            1,
            x.size,
            1f,
            x.toFloatArray(),
            x.size,
            y.toFloatArray(),
            x.size,
            0f,
            result,
            1,
            1,
        )
        return DataBuffer.create(result)
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = FloatArray(m)
        openBLAS.sgemm(
            transX,
            false,
            m,
            1,
            k,
            1f,
            x.toFloatArray(),
            if (transX) m else k,
            y.toFloatArray(),
            1,
            0f,
            result,
            1,
            1,
        )
        return DataBuffer.create(result)
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = FloatArray(n)
        openBLAS.sgemm(
            false,
            transY,
            1,
            n,
            k,
            1f,
            x.toFloatArray(),
            k,
            y.toFloatArray(),
            if (transY) k else n,
            0f,
            result,
            n,
            1,
        )
        return DataBuffer.create(result)
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
        val result = FloatArray(b * m * n)
        openBLAS.sgemm(
            transX,
            transY,
            m,
            n,
            k,
            1f,
            x.toFloatArray(),
            if (transX) m else k,
            y.toFloatArray(),
            if (transY) k else n,
            0f,
            result,
            n,
            b,
        )
        return DataBuffer.create(result)
    }
}

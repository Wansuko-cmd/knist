package com.wsr.gpu

import com.wsr.base.DataBuffer
import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "gpu"
private const val LIB_NAME = "gpu"

actual fun loadGPUBackend(): IBackend? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) GPUBackend() else null
}

class GPUBackend : IBackend by KotlinBackend {
    private val clBlast = JCLBlast()

    init {
        clBlast.init()
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = FloatArray(x.size)
        val aAddress = clBlast.transfer(x.toFloatArray(), x.size)
        val bAddress = clBlast.transfer(y.toFloatArray(), y.size)
        val cAddress = clBlast.transfer(result, result.size)
        clBlast.sgemm(
            false,
            true,
            1,
            1,
            x.size,
            1f,
            aAddress,
            x.size,
            bAddress,
            x.size,
            0f,
            cAddress,
            1,
            1,
        )
        clBlast.read(cAddress, result)
        clBlast.release(aAddress)
        clBlast.release(bAddress)
        clBlast.release(cAddress)
        return create(result)
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = FloatArray(m)
        val aAddress = clBlast.transfer(x.toFloatArray(), x.size)
        val bAddress = clBlast.transfer(y.toFloatArray(), y.size)
        val cAddress = clBlast.transfer(result, result.size)
        clBlast.sgemm(
            transX,
            false,
            m,
            1,
            k,
            1f,
            aAddress,
            if (transX) m else k,
            bAddress,
            1,
            0f,
            cAddress,
            1,
            1,
        )
        clBlast.read(cAddress, result)
        clBlast.release(aAddress)
        clBlast.release(bAddress)
        clBlast.release(cAddress)
        return create(result)
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = FloatArray(n)
        val aAddress = clBlast.transfer(x.toFloatArray(), x.size)
        val bAddress = clBlast.transfer(y.toFloatArray(), y.size)
        val cAddress = clBlast.transfer(result, result.size)
        clBlast.sgemm(
            false,
            transY,
            1,
            n,
            k,
            1f,
            aAddress,
            k,
            bAddress,
            if (transY) k else n,
            0f,
            cAddress,
            n,
            1,
        )
        clBlast.read(cAddress, result)
        clBlast.release(aAddress)
        clBlast.release(bAddress)
        clBlast.release(cAddress)
        return create(result)
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
        val aAddress = clBlast.transfer(x.toFloatArray(), x.size)
        val bAddress = clBlast.transfer(y.toFloatArray(), y.size)
        val cAddress = clBlast.transfer(result, result.size)
        clBlast.sgemm(
            transX,
            transY,
            m,
            n,
            k,
            1f,
            aAddress,
            if (transX) m else k,
            bAddress,
            if (transY) k else n,
            0f,
            cAddress,
            n,
            b,
        )
        clBlast.read(cAddress, result)
        clBlast.release(aAddress)
        clBlast.release(bAddress)
        clBlast.release(cAddress)
        return create(result)
    }
}

package com.wsr.cl

import com.wsr.base.DataBuffer
import com.wsr.base.IBLAS
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "cl"
private const val LIB_NAME = "cl_blast"

actual fun loadCLBlast(): IBLAS? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) CLBlast() else null
}

class CLBlast internal constructor() : IBLAS {
    override val isNative: Boolean = true
    private val instance = JCLBlast()

    init {
        instance.init()
    }

    override fun sdot(x: DataBuffer, y: DataBuffer): Float {
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(y.toFloatArray(), y.size)
        val result = instance.sdot(
            x.size,
            xAddress,
            1,
            yAddress,
            1,
        )
        instance.release(xAddress)
        instance.release(yAddress)
        return result
    }

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        val result = x.toFloatArray()
        val xAddress = instance.transfer(result, x.size)
        instance.sscal(
            x.size,
            alpha,
            xAddress,
            1,
        )
        instance.read(xAddress, result)
        instance.release(xAddress)
        return DataBuffer.create(result)
    }

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = y.toFloatArray()
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(result, y.size)
        instance.saxpy(
            x.size,
            alpha,
            xAddress,
            1,
            yAddress,
            1,
        )
        instance.read(yAddress, result)
        instance.release(xAddress)
        instance.release(yAddress)
        return DataBuffer.create(result)
    }

    override fun sgemv(
        row: Int,
        col: Int,
        alpha: Float,
        a: DataBuffer,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer,
    ): DataBuffer {
        val result = y.toFloatArray()
        val aAddress = instance.transfer(a.toFloatArray(), a.size)
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(result, y.size)
        instance.sgemv(
            false,
            row,
            col,
            alpha,
            aAddress,
            col,
            xAddress,
            1,
            beta,
            yAddress,
            1,
        )
        instance.read(yAddress, result)
        instance.release(aAddress)
        instance.release(xAddress)
        instance.release(yAddress)
        return DataBuffer.create(result)
    }

    override fun sgemm(
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        b: DataBuffer,
        beta: Float,
        c: DataBuffer,
        batchSize: Int,
    ): DataBuffer {
        val result = c.toFloatArray()
        val aAddress = instance.transfer(a.toFloatArray(), a.size)
        val bAddress = instance.transfer(b.toFloatArray(), b.size)
        val cAddress = instance.transfer(result, c.size)
        instance.sgemm(
            false,
            false,
            m,
            n,
            k,
            alpha,
            aAddress,
            k,
            bAddress,
            n,
            beta,
            cAddress,
            n,
            batchSize,
        )
        instance.read(cAddress, result)
        instance.release(aAddress)
        instance.release(bAddress)
        instance.release(cAddress)
        return DataBuffer.create(result)
    }
}

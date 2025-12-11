package com.wsr.cl

import com.wsr.blas.base.DataBuffer
import com.wsr.blas.base.IBLAS
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val LIB_NAME = "cl_blast"
private val LIB_PATH = createPath("cl")

actual fun loadCLBlast(): IBLAS? {
    val resource = System.mapLibraryName(LIB_NAME)
    val result = IBLAS::class.java
        .classLoader
        .getResourceAsStream(LIB_PATH + resource)
        ?.use { inputStream ->
            val path = Files
                .createTempDirectory(LIB_NAME)
                .also { it.toFile().deleteOnExit() }
                .resolve(resource)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
            System.load(path.toString())
        }
    return if (result != null) CLBlast() else null
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
            /* n = */ x.size,
            /* x = */ xAddress,
            /* incX = */ 1,
            /* y = */ yAddress,
            /* incY = */ 1,
        )
        instance.release(xAddress)
        instance.release(yAddress)
        return result
    }

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        val result = x.toFloatArray()
        val xAddress = instance.transfer(result, x.size)
        instance.sscal(
            /* n = */ x.size,
            /* alpha = */ alpha,
            /* x = */ xAddress,
            /* incX = */ 1,
        )
        instance.read(xAddress, result)
        instance.release(xAddress)
        return DataBuffer.create(result)
    }

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer,): DataBuffer {
        val result = y.toFloatArray()
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(result, y.size)
        instance.saxpy(
            /* n = */ x.size,
            /* alpha = */ alpha,
            /* x = */ xAddress,
            /* incX = */ 1,
            /* y = */ yAddress,
            /* incY = */ 1,
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
            /* trans = */ false,
            /* m = */ row,
            /* n = */ col,
            /* alpha = */ alpha,
            /* a = */ aAddress,
            /* lda = */ col,
            /* x = */ xAddress,
            /* incX = */ 1,
            /* beta = */ beta,
            /* y = */ yAddress,
            /* incY  = */ 1,
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
    ): DataBuffer {
        val result = c.toFloatArray()
        val aAddress = instance.transfer(a.toFloatArray(), a.size)
        val bAddress = instance.transfer(b.toFloatArray(), b.size)
        val cAddress = instance.transfer(result, c.size)
        instance.sgemm(
            /* transA = */ false,
            /* transB = */ false,
            /* m = */ m,
            /* n = */ n,
            /* k = */ k,
            /* alpha = */ alpha,
            /* a = */ aAddress,
            /* lda = */ k,
            /* b = */ bAddress,
            /* ldb = */ n,
            /* beta = */ beta,
            /* c = */ cAddress,
            /* ldc = */ n,
        )
        instance.read(cAddress, result)
        instance.release(aAddress)
        instance.release(bAddress)
        instance.release(cAddress)
        return DataBuffer.create(result)
    }
}

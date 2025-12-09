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

    override fun sdot(n: Int, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int): Float {
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(y.toFloatArray(), y.size)
        val result = instance.sdot(n, xAddress, incX, yAddress, incY)
        instance.release(xAddress)
        instance.release(yAddress)
        return result
    }

    override fun sscal(n: Int, alpha: Float, x: DataBuffer, incX: Int) {
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        instance.sscal(n, alpha, xAddress, incX)
        instance.read(xAddress, x.toFloatArray())
        instance.release(xAddress)
    }

    override fun saxpy(n: Int, alpha: Float, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int) {
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(y.toFloatArray(), y.size)
        instance.saxpy(n, alpha, xAddress, incX, yAddress, incY)
        instance.read(yAddress, y.toFloatArray())
        instance.release(xAddress)
        instance.release(yAddress)
    }

    override fun sgemv(
        trans: Boolean,
        m: Int,
        n: Int,
        alpha: Float,
        a: DataBuffer,
        lda: Int,
        x: DataBuffer,
        incX: Int,
        beta: Float,
        y: DataBuffer,
        incY: Int,
    ) {
        val aAddress = instance.transfer(a.toFloatArray(), a.size)
        val xAddress = instance.transfer(x.toFloatArray(), x.size)
        val yAddress = instance.transfer(y.toFloatArray(), y.size)
        instance.sgemv(
            trans,
            m,
            n,
            alpha,
            aAddress,
            lda,
            xAddress,
            incX,
            beta,
            yAddress,
            incY,
        )
        instance.read(yAddress, y.toFloatArray())
        instance.release(aAddress)
        instance.release(xAddress)
        instance.release(yAddress)
    }

    override fun sgemm(
        transA: Boolean,
        transB: Boolean,
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        lda: Int,
        b: DataBuffer,
        ldb: Int,
        beta: Float,
        c: DataBuffer,
        ldc: Int,
    ) {
        val aAddress = instance.transfer(a.toFloatArray(), a.size)
        val bAddress = instance.transfer(b.toFloatArray(), b.size)
        val cAddress = instance.transfer(c.toFloatArray(), c.size)
        instance.sgemm(
            transA,
            transB,
            m,
            n,
            k,
            alpha,
            aAddress,
            lda,
            bAddress,
            ldb,
            beta,
            cAddress,
            ldc,
        )
        instance.read(cAddress, c.toFloatArray())
        instance.release(aAddress)
        instance.release(bAddress)
        instance.release(cAddress)
    }
}

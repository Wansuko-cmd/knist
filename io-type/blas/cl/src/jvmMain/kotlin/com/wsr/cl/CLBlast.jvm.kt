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

    override fun sdot(n: Int, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int): Float =
        instance.sdot(n, x.toCLBuffer().address, incX, y.toCLBuffer().address, incY)

    override fun sscal(n: Int, alpha: Float, x: DataBuffer, incX: Int) {
        instance.sscal(n, alpha, x.toCLBuffer().address, incX)
    }

    override fun saxpy(n: Int, alpha: Float, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int) {
        instance.saxpy(n, alpha, x.toCLBuffer().address, incX, y.toCLBuffer().address, incY)
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
        instance.sgemv(
            trans,
            m,
            n,
            alpha,
            a.toCLBuffer().address,
            lda,
            x.toCLBuffer().address,
            incX,
            beta,
            y.toCLBuffer().address,
            incY,
        )
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
        instance.sgemm(
            transA,
            transB,
            m,
            n,
            k,
            alpha,
            a.toCLBuffer().address,
            lda,
            b.toCLBuffer().address,
            ldb,
            beta,
            c.toCLBuffer().address,
            ldc,
        )
    }

    inner class CLBuffer(
        override val size: Int,
        internal val address: Long,
    ): DataBuffer {
        constructor(value: FloatArray) : this(
            size = value.size,
            address = instance.transfer(value, value.size),
        )

        override val indices: IntRange = 0 until size
        private val array by lazy {
            FloatArray(size).also { instance.read(address, it) }
        }

        override fun toFloatArray(): FloatArray = array

        override fun get(i: Int): Float = array[i]

        override fun set(i: Int, value: Float) {
            TODO("Not yet implemented")
        }

        override fun slice(indices: IntRange): DataBuffer {
            TODO("Not yet implemented")
        }

        override fun copyInto(destination: DataBuffer, destinationOffset: Int) {
            TODO("Not yet implemented")
        }
    }

    private fun DataBuffer.toCLBuffer() = when (this) {
        is CLBuffer -> this
        else -> CLBuffer(this.toFloatArray())
    }
}

@file:OptIn(ExperimentalAtomicApi::class)

package com.wsr.knist.gpu

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import java.lang.ref.Cleaner
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.minusAssign

private val reservedBytes = AtomicLong(0)
internal var gpuMaxReservedBytes: Long = 4_000_000_000L

private val lastGcAtMillis = AtomicLong(0)
private const val GC_COOLDOWN_MILLIS = 500L

internal fun DataBuffer.toGPUBuffer(runtime: Long): GPUJvmBuffer = when (this) {
    is GPUJvmBuffer -> this
    else -> GPUJvmBuffer.create(this.toFloatArray(), runtime)
}

class GPUJvmBuffer(override val size: Int, internal val ptr: Long, private val runtime: Long) : DataBuffer {
    private val cleanable: Cleaner.Cleanable

    init {
        val size = size
        val ptr = ptr
        val runtime = runtime
        val byteSize = size.toLong() * Float.SIZE_BYTES
        if (reservedBytes.addAndFetch(byteSize) >= gpuMaxReservedBytes) {
            val now = System.currentTimeMillis()
            val last = lastGcAtMillis.load()
            if (now - last >= GC_COOLDOWN_MILLIS && lastGcAtMillis.compareAndSet(last, now)) System.gc()
        }
        cleanable = cleaner.register(this) {
            JBuffer.release(ptr, runtime)
            reservedBytes.minusAssign(byteSize)
        }
    }

    override fun get(i: Int): Float = JBuffer.readAll(ptr, runtime)[i]

    override fun set(i: Int, value: Float) {
        JBuffer.write(ptr, i, value, runtime)
    }

    override fun toFloatArray(): FloatArray = JBuffer.readAll(ptr, runtime)

    override fun toString(): String = toFloatArray().joinToString(prefix = "GPUJvmBuffer[", postfix = "]")

    override fun release() {
        cleanable.clean()
    }

    companion object {
        private val cleaner = Cleaner.create()

        fun create(size: Int, runtime: Long): GPUJvmBuffer {
            val ptr = JBuffer.allocate(size, runtime)
            return GPUJvmBuffer(size = size, ptr = ptr, runtime = runtime)
        }

        fun create(value: FloatArray, runtime: Long): GPUJvmBuffer {
            val ptr = JBuffer.init(value, runtime)
            return GPUJvmBuffer(size = value.size, ptr = ptr, runtime = runtime)
        }

        fun createGenerator(runtime: Long) = object : IDataBufferGenerator {
            override fun create(size: Int): DataBuffer = create(
                size = size,
                runtime = runtime,
            )

            override fun create(value: FloatArray): DataBuffer = create(
                value = value,
                runtime = runtime,
            )
        }
    }
}

package com.wsr.knist.cpu

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import java.lang.ref.Cleaner
import java.util.concurrent.atomic.AtomicLong

private val reservedBytes = AtomicLong(0)
internal var cpuMaxReservedBytes: Long = 1_500_000_000L

internal fun DataBuffer.toCPUBuffer(runtime: Long): CPUJvmBuffer = when (this) {
    is CPUJvmBuffer -> this
    else -> CPUJvmBuffer.create(this.toFloatArray(), runtime)
}

class CPUJvmBuffer private constructor(internal val ptr: Long, override val size: Int, private val runtime: Long) : DataBuffer {
    private val cleanable: Cleaner.Cleanable

    init {
        val ptr = this@CPUJvmBuffer.ptr
        val runtime = runtime
        val byteSize = size.toLong() * Float.SIZE_BYTES
        if (reservedBytes.addAndGet(byteSize) >= cpuMaxReservedBytes) System.gc()
        cleanable = cleaner.register(this) {
            JBuffer.release(ptr, runtime)
            reservedBytes.addAndGet(-byteSize)
        }
    }

    override fun get(i: Int): Float = JBuffer.get(ptr, i)

    override fun set(i: Int, value: Float) {
        JBuffer.set(ptr, i, value)
    }

    override fun toFloatArray(): FloatArray = JBuffer.readAll(ptr)

    override fun toString(): String = toFloatArray().joinToString(prefix = "CPUJvmBuffer[", postfix = "]")

    override fun release() {
        cleanable.clean()
    }

    companion object Companion {
        private val cleaner = Cleaner.create()

        fun create(size: Int, runtime: Long): CPUJvmBuffer {
            val ptr = JBuffer.allocate(size, runtime)
            return CPUJvmBuffer(ptr, size, runtime)
        }

        fun create(value: FloatArray, runtime: Long): CPUJvmBuffer {
            val ptr = JBuffer.allocate(value.size, runtime)
            JBuffer.writeAll(ptr, value)
            return CPUJvmBuffer(ptr, value.size, runtime)
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

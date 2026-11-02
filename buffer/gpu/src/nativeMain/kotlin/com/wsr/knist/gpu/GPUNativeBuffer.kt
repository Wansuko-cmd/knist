@file:OptIn(ExperimentalForeignApi::class, ExperimentalAtomicApi::class, ExperimentalNativeApi::class)

package com.wsr.knist.gpu

import cnames.structs.GPUBuffer
import cnames.structs.Runtime
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_allocate
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_init
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_read_all
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_release
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_write
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

internal fun DataBuffer.toGPUBuffer(runtime: CPointer<Runtime>): GPUNativeBuffer = when (this) {
    is GPUNativeBuffer -> this
    else -> GPUNativeBuffer.create(this.toFloatArray(), runtime)
}

private class GPUNativeBufferState(val ptr: CPointer<GPUBuffer>, val runtime: CPointer<Runtime>, val released: AtomicInt) {
    fun release() {
        if (released.compareAndExchange(0, 1) == 0) {
            com_wsr_gpu_buffer_release(ptr, runtime)
        }
    }
}

class GPUNativeBuffer(val buffer: CPointer<GPUBuffer>, override val size: Int, private val runtime: CPointer<Runtime>) : DataBuffer {
    private val state = GPUNativeBufferState(buffer, runtime, AtomicInt(0))

    @Suppress("UNUSED")
    private val cleaner = createCleaner(state) { it.release() }

    override fun get(i: Int): Float = toFloatArray()[i]

    override fun set(i: Int, value: Float) {
        com_wsr_gpu_buffer_write(buffer, i, value, runtime)
    }

    override fun toFloatArray(): FloatArray = FloatArray(size).also { array ->
        if (array.isNotEmpty()) {
            array.usePinned { pinned ->
                com_wsr_gpu_buffer_read_all(buffer, runtime, pinned.addressOf(0), size)
            }
        }
    }

    override fun toString(): String = toFloatArray().joinToString(prefix = "GPUNativeBuffer[", postfix = "]")

    override fun release() {
        state.release()
    }

    companion object {
        fun create(size: Int, runtime: CPointer<Runtime>) = GPUNativeBuffer(
            buffer = com_wsr_gpu_buffer_allocate(size, runtime)!!,
            size = size,
            runtime = runtime,
        )

        fun create(value: FloatArray, runtime: CPointer<Runtime>): GPUNativeBuffer {
            val buffer = if (value.isEmpty()) {
                com_wsr_gpu_buffer_allocate(0, runtime)!!
            } else {
                value.usePinned { pinned ->
                    com_wsr_gpu_buffer_init(pinned.addressOf(0), value.size, runtime)!!
                }
            }
            return GPUNativeBuffer(buffer = buffer, size = value.size, runtime = runtime)
        }

        fun createGenerator(runtime: CPointer<Runtime>) = object : IDataBufferGenerator {
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

@file:OptIn(ExperimentalForeignApi::class)

package com.wsr.knist.cpu

import cnames.structs.CPUBuffer
import cnames.structs.Runtime
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_allocate
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_get
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_init
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_read_all
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_release
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_set
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

internal fun DataBuffer.toCPUBuffer(runtime: CPointer<Runtime>): CPUNativeBuffer = when (this) {
    is CPUNativeBuffer -> this
    else -> CPUNativeBuffer.create(this.toFloatArray(), runtime)
}

@OptIn(ExperimentalAtomicApi::class)
private class CPUNativeBufferState(val ptr: CPointer<CPUBuffer>, val released: AtomicInt, val runtime: CPointer<Runtime>)

@OptIn(ExperimentalAtomicApi::class)
class CPUNativeBuffer(val buffer: CPointer<CPUBuffer>, override val size: Int, private val runtime: CPointer<Runtime>) : DataBuffer {
    private val state = CPUNativeBufferState(buffer, AtomicInt(0), runtime)

    @Suppress("UNUSED")
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(state) { s ->
        if (s.released.compareAndExchange(0, 1) == 0) {
            com_wsr_cpu_buffer_release(s.ptr, s.runtime)
        }
    }

    override fun get(i: Int): Float = com_wsr_cpu_buffer_get(buffer, i)

    override fun set(i: Int, value: Float) {
        com_wsr_cpu_buffer_set(buffer, i, value)
    }

    override fun toFloatArray(): FloatArray = FloatArray(size).also { array ->
        array.usePinned { pinned ->
            com_wsr_cpu_buffer_read_all(buffer, pinned.addressOf(0))
        }
    }

    override fun toString(): String = toFloatArray().joinToString(prefix = "CPUNativeBuffer[", postfix = "]")

    override fun release() {
        if (state.released.compareAndExchange(0, 1) == 0) {
            com_wsr_cpu_buffer_release(buffer, runtime)
        }
    }

    companion object {
        fun create(size: Int, runtime: CPointer<Runtime>) = CPUNativeBuffer(
            buffer = com_wsr_cpu_buffer_allocate(size, runtime)!!,
            size = size,
            runtime = runtime,
        )

        fun create(value: FloatArray, runtime: CPointer<Runtime>): CPUNativeBuffer {
            val buffer = value.usePinned { pinned ->
                com_wsr_cpu_buffer_init(pinned.addressOf(0), value.size, runtime)!!
            }
            return CPUNativeBuffer(buffer = buffer, size = value.size, runtime = runtime)
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

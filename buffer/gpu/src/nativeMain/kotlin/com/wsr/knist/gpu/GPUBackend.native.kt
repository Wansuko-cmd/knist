@file:OptIn(ExperimentalForeignApi::class)

package com.wsr.knist.gpu

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.KotlinBackend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.base.data.size
import com.wsr.knist.gpu.rs.com_wsr_gpu_average_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_average_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_average_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_buffer_allocate
import com.wsr.knist.gpu.rs.com_wsr_gpu_copy_into_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_copy_into_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_copy_into_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d0_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d1_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d1_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d2_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d2_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d3_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d3_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d3_to_d4
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d4_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d4_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_div_d4_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_equals_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_equals_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_exp
import com.wsr.knist.gpu.rs.com_wsr_gpu_flip_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_fold_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_fold_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_gather
import com.wsr.knist.gpu.rs.com_wsr_gpu_greater_than_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_greater_than_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_less_than_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_less_than_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_ln
import com.wsr.knist.gpu.rs.com_wsr_gpu_mat_mul
import com.wsr.knist.gpu.rs.com_wsr_gpu_max_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_max_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_max_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_min_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_min_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_min_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d0_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d1_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d1_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d2_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d2_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d3_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d3_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d3_to_d4
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d4_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d4_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_minus_d4_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d0_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d1_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d1_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d2_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d2_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d3_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d3_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d3_to_d4
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d4_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d4_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_plus_d4_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_pow
import com.wsr.knist.gpu.rs.com_wsr_gpu_profiler_take_cpu
import com.wsr.knist.gpu.rs.com_wsr_gpu_profiler_take_gpu
import com.wsr.knist.gpu.rs.com_wsr_gpu_random
import com.wsr.knist.gpu.rs.com_wsr_gpu_runtime_allocate
import com.wsr.knist.gpu.rs.com_wsr_gpu_runtime_flush
import com.wsr.knist.gpu.rs.com_wsr_gpu_runtime_sync
import com.wsr.knist.gpu.rs.com_wsr_gpu_scatter_add
import com.wsr.knist.gpu.rs.com_wsr_gpu_sigmoid
import com.wsr.knist.gpu.rs.com_wsr_gpu_slice_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_slice_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_slice_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_sqrt
import com.wsr.knist.gpu.rs.com_wsr_gpu_sum_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_sum_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_sum_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d0_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d1_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d1_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d1_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d2_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d2_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d3_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d3_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d3_to_d4
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d4_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d4_to_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_times_d4_to_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_transpose_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_transpose_d3
import com.wsr.knist.gpu.rs.com_wsr_gpu_transpose_d4
import com.wsr.knist.gpu.rs.com_wsr_gpu_unfold_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_unfold_d2
import com.wsr.knist.gpu.rs.com_wsr_gpu_where_d0_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_where_d0_to_d1
import com.wsr.knist.gpu.rs.com_wsr_gpu_where_d1_to_d0
import com.wsr.knist.gpu.rs.com_wsr_gpu_where_d1_to_d1
import kotlin.random.Random
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped

actual fun loadGPUBackend(fallback: IBackend, maxReservedBytes: Long, maxPoolBytes: Long, enableProfiler: Boolean): IBackend = GPUNativeBackend(fallback, maxPoolBytes, enableProfiler)

class GPUNativeBackend(private val fallback: IBackend, maxPoolBytes: Long, enableProfiler: Boolean = false) : IBackend by fallback {
    private val runtime = com_wsr_gpu_runtime_allocate(pool_size = maxPoolBytes, enable_profiler = enableProfiler)!!
    override val generator: IDataBufferGenerator = GPUNativeBuffer.createGenerator(runtime = runtime)

    fun takeCPUProfileNs(): Long = com_wsr_gpu_profiler_take_cpu(runtime)

    fun takeGPUProfileNs(): GPUProfilerResult = memScoped {
        val result = allocArray<LongVar>(2)
        com_wsr_gpu_profiler_take_gpu(runtime, result)
        GPUProfilerResult(busyNs = result[0], spanNs = result[1])
    }

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_plus_d0_to_d1(x = x, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d1_to_d1(x = x.toGPUBuffer().buffer, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_plus_d1_to_d2(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_plus_d1_to_d3(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d2_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_plus_d2_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d3_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d3_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_plus_d3_to_d4(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d4_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d4_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_plus_d4_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_minus_d0_to_d1(x = x, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d1_to_d1(x = x.toGPUBuffer().buffer, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_minus_d1_to_d2(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_minus_d1_to_d3(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d2_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_minus_d2_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d3_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d3_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_minus_d3_to_d4(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d4_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d4_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_minus_d4_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_times_d0_to_d1(x = x, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d1_to_d1(x = x.toGPUBuffer().buffer, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_times_d1_to_d2(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_times_d1_to_d3(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d2_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_times_d2_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d3_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d3_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_times_d3_to_d4(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d4_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d4_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_times_d4_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_div_d0_to_d1(x = x, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d1_to_d1(x = x.toGPUBuffer().buffer, y = y.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_div_d1_to_d2(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_div_d1_to_d3(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d2_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_div_d2_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d3_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d3_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_div_d3_to_d4(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d4_to_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d4_to_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_div_d4_to_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = GPUNativeBuffer.create(b)
        com_wsr_gpu_mat_mul(
            x = x.toGPUBuffer().buffer,
            trans_x = false,
            y = y.toGPUBuffer().buffer,
            trans_y = false,
            m = 1,
            n = 1,
            k = x.size / b,
            b = b,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = GPUNativeBuffer.create(n)
        com_wsr_gpu_mat_mul(
            x = x.toGPUBuffer().buffer,
            trans_x = false,
            y = y.toGPUBuffer().buffer,
            trans_y = transY,
            m = 1,
            n = n,
            k = k,
            b = 1,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = GPUNativeBuffer.create(m)
        com_wsr_gpu_mat_mul(
            x = x.toGPUBuffer().buffer,
            trans_x = transX,
            y = y.toGPUBuffer().buffer,
            trans_y = false,
            m = m,
            n = 1,
            k = k,
            b = 1,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        val result = GPUNativeBuffer.create(b * m * n)
        com_wsr_gpu_mat_mul(
            x = x.toGPUBuffer().buffer,
            trans_x = transX,
            y = y.toGPUBuffer().buffer,
            trans_y = transY,
            m = m,
            n = n,
            k = k,
            b = b,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_exp(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_ln(x = x.toGPUBuffer().buffer, e = e, result = result.buffer, runtime = runtime)
        return result
    }

    override fun sigmoid(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_sigmoid(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_pow(x = x.toGPUBuffer().buffer, n = n, result = result.buffer, runtime = runtime)
        return result
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_sqrt(x = x.toGPUBuffer().buffer, e = e, result = result.buffer, runtime = runtime)
        return result
    }

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        val result = GPUNativeBuffer.create(size)
        com_wsr_gpu_random(from = from, until = until, seed = random.nextLong(), result = result.buffer, runtime = runtime)
        return result
    }

    override fun average(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(1)
        com_wsr_gpu_average_d1(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_gpu_average_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_gpu_average_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun max(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(1)
        com_wsr_gpu_max_d1(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_gpu_max_d2(x = x.toGPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer, runtime = runtime)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_gpu_max_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun min(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(1)
        com_wsr_gpu_min_d1(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_gpu_min_d2(x = x.toGPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer, runtime = runtime)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_gpu_min_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun sum(x: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(1)
        com_wsr_gpu_sum_d1(x = x.toGPUBuffer().buffer, result = result.buffer, runtime = runtime)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_gpu_sum_d2(x = x.toGPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer, runtime = runtime)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_gpu_sum_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun maxIndex(x: DataBuffer): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.maxIndex(x)
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.maxIndex(x, xi, xj, axis)
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.maxIndex(x, xi, xj, xk, axis)
    }

    override fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topK(x, k, random)
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topK(x, xi, xj, k, axis, random)
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topK(x, xi, xj, xk, k, axis, random)
    }

    override fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topP(x, p, random)
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topP(x, xi, xj, p, axis, random)
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val x = fallback.generator.create(x.toFloatArray())
        return fallback.topP(x, xi, xj, xk, p, axis, random)
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_transpose_d2(x = x.toGPUBuffer().buffer, xi = xi, xj = xj, result = result.buffer, runtime = runtime)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_transpose_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis_i = axisI,
            axis_j = axisJ,
            axis_k = axisK,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_transpose_d4(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            axis_i = axisI,
            axis_j = axisJ,
            axis_k = axisK,
            axis_l = axisL,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer {
        val result = GPUNativeBuffer.create(kotlin.math.min(x.size, indices.size))
        com_wsr_gpu_slice_d1(
            x = x.toGPUBuffer().buffer,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> kotlin.math.min(xi, indices.size) * xj
                else -> xi * kotlin.math.min(xj, indices.size)
            },
        )
        com_wsr_gpu_slice_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = GPUNativeBuffer.create(
            size = when (axis) {
                0 -> kotlin.math.min(xi, indices.size) * xj * xk
                1 -> xi * kotlin.math.min(xj, indices.size) * xk
                else -> xi * xj * kotlin.math.min(xk, indices.size)
            },
        )
        com_wsr_gpu_slice_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        when (y) {
            is GPUNativeBuffer -> {
                com_wsr_gpu_copy_into_d1(
                    x = x.toGPUBuffer().buffer,
                    result = y.buffer,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                    runtime = runtime,
                )
            }

            else -> KotlinBackend.copyInto(x = x, y = y, indices = indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is GPUNativeBuffer -> {
                com_wsr_gpu_copy_into_d2(
                    x = x.toGPUBuffer().buffer,
                    result = y.buffer,
                    ri = yi,
                    rj = yj,
                    axis = axis,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                    runtime = runtime,
                )
            }

            else -> {
                fallback.copyInto(x = x, y = y, yi = yi, yj = yj, axis = axis, indices = indices)
            }
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is GPUNativeBuffer -> {
                com_wsr_gpu_copy_into_d3(
                    x = x.toGPUBuffer().buffer,
                    result = y.buffer,
                    ri = yi,
                    rj = yj,
                    rk = yk,
                    axis = axis,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                    runtime = runtime,
                )
            }

            else -> {
                fallback.copyInto(x = x, y = y, yi = yi, yj = yj, yk = yk, axis = axis, indices = indices)
            }
        }
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        val result = GPUNativeBuffer.create(i * x.size * k)
        com_wsr_gpu_gather(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            i = i,
            j = j,
            k = k,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        val result = GPUNativeBuffer.create(i * j * k)
        com_wsr_gpu_scatter_add(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            i = i,
            j = j,
            k = k,
            b = b,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_greater_than_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_greater_than_d1_to_d1(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_less_than_d1_to_d0(x = x.toGPUBuffer().buffer, y = y, result = result.buffer, runtime = runtime)
        return result
    }

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_less_than_d1_to_d1(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_equals_d1_to_d0(
            x = x.toGPUBuffer().buffer,
            y = y,
            atol = absoluteTolerance,
            rtol = relativeTolerance,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_equals_d1_to_d1(
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            atol = absoluteTolerance,
            rtol = relativeTolerance,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(condition.size)
        com_wsr_gpu_where_d0_to_d0(
            condition = condition.toGPUBuffer().buffer,
            x = x,
            y = y,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(y.size)
        com_wsr_gpu_where_d0_to_d1(
            condition = condition.toGPUBuffer().buffer,
            x = x,
            y = y.toGPUBuffer().buffer,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_where_d1_to_d0(
            condition = condition.toGPUBuffer().buffer,
            x = x.toGPUBuffer().buffer,
            y = y,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_where_d1_to_d1(
            condition = condition.toGPUBuffer().buffer,
            x = x.toGPUBuffer().buffer,
            y = y.toGPUBuffer().buffer,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUNativeBuffer.create(x.size)
        com_wsr_gpu_flip_d3(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val rj = (xj - windowSize + padding * 2) / stride + 1
        val result = GPUNativeBuffer.create(b * xi * rj * window)
        com_wsr_gpu_unfold_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val rj = (xj - windowSize + padding * 2) / stride + 1
        val rk = (xk - windowSize + padding * 2) / stride + 1
        val result = GPUNativeBuffer.create(b * xi * rj * rk * window * window)
        com_wsr_gpu_unfold_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (xk - 1) * dilation + 1
        val nj = windowSize + (xj - 1) * stride - padding * 2
        val result = GPUNativeBuffer.create(b * xi * nj)
        com_wsr_gpu_fold_d1(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val window = kotlin.math.sqrt(xl.toDouble()).toInt()
        val windowSize = (window - 1) * dilation + 1
        val nj = windowSize + (xj - 1) * stride - padding * 2
        val nk = windowSize + (xk - 1) * stride - padding * 2
        val result = GPUNativeBuffer.create(b * xi * nj * nk)
        com_wsr_gpu_fold_d2(
            x = x.toGPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
            runtime = runtime,
        )
        return result
    }

    override fun flush() = com_wsr_gpu_runtime_flush(runtime)
    override fun sync() = com_wsr_gpu_runtime_sync(runtime)

    private fun GPUNativeBuffer.Companion.create(size: Int) = GPUNativeBuffer.create(
        size = size,
        runtime = runtime,
    )

    private fun DataBuffer.toGPUBuffer(): GPUNativeBuffer = toGPUBuffer(runtime = runtime)
}

@file:OptIn(ExperimentalForeignApi::class)

package com.wsr.knist.cpu

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.size
import com.wsr.knist.cpu.rs.com_wsr_cpu_average_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_average_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_average_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_allocate
import com.wsr.knist.cpu.rs.com_wsr_cpu_buffer_init
import com.wsr.knist.cpu.rs.com_wsr_cpu_copy_into_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_copy_into_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_copy_into_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d0_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d1_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d1_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d2_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d2_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d3_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d3_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d3_to_d4
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d4_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d4_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_div_d4_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_equals_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_equals_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_exp_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_flip_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_fold_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_fold_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_gather
import com.wsr.knist.cpu.rs.com_wsr_cpu_greater_than_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_greater_than_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_inner
import com.wsr.knist.cpu.rs.com_wsr_cpu_less_than_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_less_than_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_ln_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_mat_mul_d1_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_mat_mul_d2_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_mat_mul_d2_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_index_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_index_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_max_index_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_min_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_min_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_min_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d0_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d1_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d1_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d2_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d2_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d3_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d3_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d3_to_d4
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d4_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d4_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_minus_d4_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d0_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d1_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d1_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d2_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d2_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d3_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d3_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d3_to_d4
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d4_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d4_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_plus_d4_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_pow_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_random_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_runtime_allocate
import com.wsr.knist.cpu.rs.com_wsr_cpu_scatter_add
import com.wsr.knist.cpu.rs.com_wsr_cpu_sigmoid_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_slice_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_slice_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_slice_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_sqrt_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_sum_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_sum_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_sum_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d0_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d1_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d1_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d1_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d2_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d2_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d3_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d3_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d3_to_d4
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d4_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d4_to_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_times_d4_to_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_k_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_k_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_k_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_p_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_p_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_top_p_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_transpose_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_transpose_d3
import com.wsr.knist.cpu.rs.com_wsr_cpu_transpose_d4
import com.wsr.knist.cpu.rs.com_wsr_cpu_transpose_d5
import com.wsr.knist.cpu.rs.com_wsr_cpu_unfold_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_unfold_d2
import com.wsr.knist.cpu.rs.com_wsr_cpu_where_d0_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_where_d0_to_d1
import com.wsr.knist.cpu.rs.com_wsr_cpu_where_d1_to_d0
import com.wsr.knist.cpu.rs.com_wsr_cpu_where_d1_to_d1
import kotlin.math.min
import kotlin.random.Random
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

actual fun loadCPUBackend(fallback: IBackend, maxReservedBytes: Long, maxPoolBytes: Long): IBackend = CPUNativeBackend(fallback, maxPoolBytes)

class CPUNativeBackend(private val fallback: IBackend, maxPoolBytes: Long) : IBackend by fallback {
    private val runtime = com_wsr_cpu_runtime_allocate(pool_size = maxPoolBytes)!!
    override val generator = CPUNativeBuffer.createGenerator(runtime = runtime)

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_plus_d0_to_d1(x = x, y = y.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d1_to_d0(x = x.toCPUBuffer().buffer, y = y, result = result.buffer)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_plus_d1_to_d2(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_plus_d1_to_d3(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d2_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_plus_d2_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d3_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d3_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_plus_d3_to_d4(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d4_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d4_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_plus_d4_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_minus_d0_to_d1(x = x, y = y.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d1_to_d0(x = x.toCPUBuffer().buffer, y = y, result = result.buffer)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_minus_d1_to_d2(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_minus_d1_to_d3(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d2_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_minus_d2_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d3_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d3_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_minus_d3_to_d4(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d4_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d4_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_minus_d4_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_times_d0_to_d1(x = x, y = y.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d1_to_d0(x = x.toCPUBuffer().buffer, y = y, result = result.buffer)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_times_d1_to_d2(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_times_d1_to_d3(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d2_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_times_d2_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d3_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d3_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_times_d3_to_d4(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d4_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d4_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_times_d4_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_div_d0_to_d1(x = x, y = y.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d1_to_d0(x = x.toCPUBuffer().buffer, y = y, result = result.buffer)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_div_d1_to_d2(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_div_d1_to_d3(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d2_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_div_d2_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d3_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d3_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_div_d3_to_d4(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d4_to_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d4_to_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.buffer,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_div_d4_to_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toCPUBuffer().buffer,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.buffer,
        )
        return result
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = CPUNativeBuffer.create(b)
        com_wsr_cpu_inner(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            b = b,
            result = result.buffer,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = CPUNativeBuffer.create(m)
        com_wsr_cpu_mat_mul_d2_to_d1(
            x = x.toCPUBuffer().buffer,
            trans_x = transX,
            y = y.toCPUBuffer().buffer,
            m = m,
            k = k,
            result = result.buffer,
        )
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = CPUNativeBuffer.create(n)
        com_wsr_cpu_mat_mul_d1_to_d2(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            trans_y = transY,
            n = n,
            k = k,
            result = result.buffer,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        val result = CPUNativeBuffer.create(b * m * n)
        com_wsr_cpu_mat_mul_d2_to_d2(
            x = x.toCPUBuffer().buffer,
            trans_x = transX,
            y = y.toCPUBuffer().buffer,
            trans_y = transY,
            m = m,
            n = n,
            k = k,
            b = b,
            result = result.buffer,
        )
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_exp_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_ln_d1(x = x.toCPUBuffer().buffer, e = e, result = result.buffer)
        return result
    }

    override fun sigmoid(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_sigmoid_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_pow_d1(x = x.toCPUBuffer().buffer, n = n, result = result.buffer)
        return result
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_sqrt_d1(x = x.toCPUBuffer().buffer, e = e, result = result.buffer)
        return result
    }

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(size)
        com_wsr_cpu_random_d1(from = from, until = until, seed = random.nextLong(), result = result.buffer)
        return result
    }

    override fun average(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_average_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_average_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_average_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun max(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_max_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_max_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_max_d3(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, xk = xk, axis = axis, result = result.buffer)
        return result
    }

    override fun min(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_min_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_min_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_min_d3(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, xk = xk, axis = axis, result = result.buffer)
        return result
    }

    override fun sum(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_sum_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_sum_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_sum_d3(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, xk = xk, axis = axis, result = result.buffer)
        return result
    }

    override fun maxIndex(x: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_max_index_d1(x = x.toCPUBuffer().buffer, result = result.buffer)
        return result
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_max_index_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, axis = axis, result = result.buffer)
        return result
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_max_index_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    override fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_top_k_d1(
            x = x.toCPUBuffer().buffer,
            k = k,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_top_k_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            k = k,
            axis = axis,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_top_k_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            k = k,
            axis = axis,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(1)
        com_wsr_cpu_top_p_d1(
            x = x.toCPUBuffer().buffer,
            p = p,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        com_wsr_cpu_top_p_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            p = p,
            axis = axis,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        com_wsr_cpu_top_p_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            p = p,
            axis = axis,
            seed = random.nextLong(),
            result = result.buffer,
        )
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_transpose_d2(x = x.toCPUBuffer().buffer, xi = xi, xj = xj, result = result.buffer)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_transpose_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis_i = axisI,
            axis_j = axisJ,
            axis_k = axisK,
            result = result.buffer,
        )
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_transpose_d4(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            axis_i = axisI,
            axis_j = axisJ,
            axis_k = axisK,
            axis_l = axisL,
            result = result.buffer,
        )
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, xm: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, axisM: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_transpose_d5(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            xm = xm,
            axis_i = axisI,
            axis_j = axisJ,
            axis_k = axisK,
            axis_l = axisL,
            axis_m = axisM,
            result = result.buffer,
        )
        return result
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer {
        val result = CPUNativeBuffer.create(min(x.size, indices.size))
        com_wsr_cpu_slice_d1(
            x = x.toCPUBuffer().buffer,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.toCPUBuffer().buffer,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> min(xi, indices.size) * xj
                else -> xi * min(xj, indices.size)
            },
        )
        com_wsr_cpu_slice_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.toCPUBuffer().buffer,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = CPUNativeBuffer.create(
            size = when (axis) {
                0 -> min(xi, indices.size) * xj * xk
                1 -> xi * min(xj, indices.size) * xk
                else -> xi * xj * min(xk, indices.size)
            },
        )
        com_wsr_cpu_slice_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.toCPUBuffer().buffer,
        )
        return result
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        when (y) {
            is CPUNativeBuffer -> {
                com_wsr_cpu_copy_into_d1(
                    x = x.toCPUBuffer().buffer,
                    result = y.buffer,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                )
            }

            else -> fallback.copyInto(x, y, indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is CPUNativeBuffer -> {
                com_wsr_cpu_copy_into_d2(
                    x = x.toCPUBuffer().buffer,
                    result = y.buffer,
                    ri = yi,
                    rj = yj,
                    axis = axis,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                )
            }

            else -> fallback.copyInto(x, y, yi, yj, axis, indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is CPUNativeBuffer -> {
                com_wsr_cpu_copy_into_d3(
                    x = x.toCPUBuffer().buffer,
                    result = y.buffer,
                    ri = yi,
                    rj = yj,
                    rk = yk,
                    axis = axis,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                )
            }

            else -> fallback.copyInto(x, y, yi, yj, yk, axis, indices)
        }
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        val n = x.size
        val result = CPUNativeBuffer.create(i * n * k)
        com_wsr_cpu_gather(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            i = i,
            j = j,
            k = k,
            result = result.buffer,
        )
        return result
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        val result = CPUNativeBuffer.create(i * j * k)
        com_wsr_cpu_scatter_add(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            i = i,
            j = j,
            k = k,
            b = b,
            result = result.buffer,
        )
        return result
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_greater_than_d1_to_d0(
            x = x.toCPUBuffer().buffer,
            y = y,
            result = result.buffer,
        )
        return result
    }

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_greater_than_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_less_than_d1_to_d0(
            x = x.toCPUBuffer().buffer,
            y = y,
            result = result.buffer,
        )
        return result
    }

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_less_than_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_equals_d1_to_d0(
            x = x.toCPUBuffer().buffer,
            y = y,
            atol = absoluteTolerance,
            rtol = relativeTolerance,
            result = result.buffer,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_equals_d1_to_d1(
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            atol = absoluteTolerance,
            rtol = relativeTolerance,
            result = result.buffer,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(condition.size)
        com_wsr_cpu_where_d0_to_d0(
            condition = condition.toCPUBuffer().buffer,
            x = x,
            y = y,
            result = result.buffer,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(y.size)
        com_wsr_cpu_where_d0_to_d1(
            condition = condition.toCPUBuffer().buffer,
            x = x,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_where_d1_to_d0(
            condition = condition.toCPUBuffer().buffer,
            x = x.toCPUBuffer().buffer,
            y = y,
            result = result.buffer,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_where_d1_to_d1(
            condition = condition.toCPUBuffer().buffer,
            x = x.toCPUBuffer().buffer,
            y = y.toCPUBuffer().buffer,
            result = result.buffer,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj + padding * 2 - windowSize) / stride + 1
        val result = CPUNativeBuffer.create(b * xi * oj * window)
        com_wsr_cpu_unfold_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj + padding * 2 - windowSize) / stride + 1
        val ok = (xk + padding * 2 - windowSize) / stride + 1
        val ww = window * window
        val result = CPUNativeBuffer.create(b * xi * oj * ok * ww)
        com_wsr_cpu_unfold_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (xk - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val result = CPUNativeBuffer.create(b * xi * oj)
        com_wsr_cpu_fold_d1(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val window = kotlin.math.sqrt(xl.toDouble()).toInt()
        val windowSize = (window - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val ok = windowSize + (xk - 1) * stride - padding * 2
        val result = CPUNativeBuffer.create(b * xi * oj * ok)
        com_wsr_cpu_fold_d2(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.buffer,
        )
        return result
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUNativeBuffer.create(x.size)
        com_wsr_cpu_flip_d3(
            x = x.toCPUBuffer().buffer,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.buffer,
        )
        return result
    }

    private fun CPUNativeBuffer.Companion.create(size: Int) = CPUNativeBuffer(
        buffer = com_wsr_cpu_buffer_allocate(size, runtime)!!,
        size = size,
        runtime = runtime,
    )

    private fun CPUNativeBuffer.Companion.create(value: FloatArray): CPUNativeBuffer {
        val buffer = value.usePinned { pinned ->
            com_wsr_cpu_buffer_init(pinned.addressOf(0), value.size, runtime)!!
        }
        return CPUNativeBuffer(buffer = buffer, size = value.size, runtime = runtime)
    }

    private fun DataBuffer.toCPUBuffer() = toCPUBuffer(runtime = runtime)
}

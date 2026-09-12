package com.wsr.knist.gpu

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.KotlinBackend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.IDataBufferGenerator
import com.wsr.knist.base.data.size
import com.wsr.knist.gpu.elementwise.compare.JCompare
import com.wsr.knist.gpu.elementwise.compare.where.JWhere
import com.wsr.knist.gpu.elementwise.generator.JGenerator
import com.wsr.knist.gpu.elementwise.math.JMath
import com.wsr.knist.gpu.elementwise.operation.JDiv
import com.wsr.knist.gpu.elementwise.operation.JMinus
import com.wsr.knist.gpu.elementwise.operation.JPlus
import com.wsr.knist.gpu.elementwise.operation.JTimes
import com.wsr.knist.gpu.index.JIndex
import com.wsr.knist.gpu.linalg.JMatMul
import com.wsr.knist.gpu.reduction.JReduction
import com.wsr.knist.gpu.shape.JShape
import kotlin.random.Random

class GPUBackend(private val fallback: IBackend, maxPoolBytes: Long, enableProfiler: Boolean = false) : IBackend by fallback {
    private val runtime = JRuntime.allocate(maxPoolBytes, enableProfiler)
    override val generator: IDataBufferGenerator = GPUJvmBuffer.createGenerator(runtime = runtime)

    fun takeCPUProfileNs(): Long = JProfiler.takeCPU(runtime)

    fun takeGPUProfileNs(): GPUProfilerResult {
        val result = JProfiler.takeGPU(runtime)
        return GPUProfilerResult(busyNs = result[0], spanNs = result[1])
    }

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JPlus.plusD0ToD1(x = x, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD1ToD1(x = x.toGPUBuffer().ptr, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JPlus.plusD1ToD2(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JPlus.plusD1ToD3(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD2ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JPlus.plusD2ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD3ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD3ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JPlus.plusD3ToD4(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JMinus.minusD0ToD1(x = x, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD1ToD1(x = x.toGPUBuffer().ptr, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JMinus.minusD1ToD2(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JMinus.minusD1ToD3(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD2ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JMinus.minusD2ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD3ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD3ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JMinus.minusD3ToD4(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JTimes.timesD0ToD1(x = x, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD1ToD1(x = x.toGPUBuffer().ptr, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JTimes.timesD1ToD2(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JTimes.timesD1ToD3(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD2ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JTimes.timesD2ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD3ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD3ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JTimes.timesD3ToD4(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JDiv.divD0ToD1(x = x, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD1ToD1(x = x.toGPUBuffer().ptr, y = y.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JDiv.divD1ToD2(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JDiv.divD1ToD3(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD2ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JDiv.divD2ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD3ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD3ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JDiv.divD3ToD4(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            yl = yl,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD4ToD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD4ToD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            axis1 = axis1,
            axis2 = axis2,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JDiv.divD4ToD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            y = y.toGPUBuffer().ptr,
            yi = yi,
            yj = yj,
            yk = yk,
            axis1 = axis1,
            axis2 = axis2,
            axis3 = axis3,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = GPUJvmBuffer.create(b)
        JMatMul.matMul(
            x = x.toGPUBuffer().ptr,
            transX = false,
            y = y.toGPUBuffer().ptr,
            transY = false,
            m = 1,
            n = 1,
            k = x.size / b,
            b = b,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = GPUJvmBuffer.create(n)
        JMatMul.matMul(
            x = x.toGPUBuffer().ptr,
            transX = false,
            y = y.toGPUBuffer().ptr,
            transY = transY,
            m = 1,
            n = n,
            k = k,
            b = 1,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = GPUJvmBuffer.create(m)
        JMatMul.matMul(
            x = x.toGPUBuffer().ptr,
            transX = transX,
            y = y.toGPUBuffer().ptr,
            transY = false,
            m = m,
            n = 1,
            k = k,
            b = 1,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        val result = GPUJvmBuffer.create(b * m * n)
        JMatMul.matMul(
            x = x.toGPUBuffer().ptr,
            transX = transX,
            y = y.toGPUBuffer().ptr,
            transY = transY,
            m = m,
            n = n,
            k = k,
            b = b,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMath.exp(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMath.ln(x = x.toGPUBuffer().ptr, e = e, result = result.ptr, runtime = runtime)
        return result
    }

    override fun sigmoid(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMath.sigmoid(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMath.pow(x = x.toGPUBuffer().ptr, n = n, result = result.ptr, runtime = runtime)
        return result
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JMath.sqrt(x = x.toGPUBuffer().ptr, e = e, result = result.ptr, runtime = runtime)
        return result
    }

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        val result = GPUJvmBuffer.create(size)
        JGenerator.random(from = from, until = until, seed = random.nextLong(), result = result.ptr, runtime = runtime)
        return result
    }

    override fun average(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(1)
        JReduction.averageD1(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.averageD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.averageD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun max(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(1)
        JReduction.maxD1(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.maxD2(x = x.toGPUBuffer().ptr, xi = xi, xj = xj, axis = axis, result = result.ptr, runtime = runtime)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.maxD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun min(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(1)
        JReduction.minD1(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.minD2(x = x.toGPUBuffer().ptr, xi = xi, xj = xj, axis = axis, result = result.ptr, runtime = runtime)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.minD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun sum(x: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(1)
        JReduction.sumD1(x = x.toGPUBuffer().ptr, result = result.ptr, runtime = runtime)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.sumD2(x = x.toGPUBuffer().ptr, xi = xi, xj = xj, axis = axis, result = result.ptr, runtime = runtime)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.sumD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.ptr,
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
        val result = GPUJvmBuffer.create(x.size)
        JShape.transposeD2(x = x.toGPUBuffer().ptr, xi = xi, xj = xj, result = result.ptr, runtime = runtime)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JShape.transposeD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axisI = axisI,
            axisJ = axisJ,
            axisK = axisK,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JShape.transposeD4(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            axisI = axisI,
            axisJ = axisJ,
            axisK = axisK,
            axisL = axisL,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer {
        val result = GPUJvmBuffer.create(kotlin.math.min(x.size, indices.size))
        JShape.sliceD1(
            x = x.toGPUBuffer().ptr,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> kotlin.math.min(xi, indices.size) * xj
                else -> xi * kotlin.math.min(xj, indices.size)
            },
        )
        JShape.sliceD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = GPUJvmBuffer.create(
            size = when (axis) {
                0 -> kotlin.math.min(xi, indices.size) * xj * xk
                1 -> xi * kotlin.math.min(xj, indices.size) * xk
                else -> xi * xj * kotlin.math.min(xk, indices.size)
            },
        )
        JShape.sliceD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            start = indices.first,
            end = indices.last,
            step = indices.step,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        when (y) {
            is GPUJvmBuffer -> {
                JShape.copyIntoD1(
                    x = x.toGPUBuffer().ptr,
                    result = y.ptr,
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
            is GPUJvmBuffer -> {
                JShape.copyIntoD2(
                    x = x.toGPUBuffer().ptr,
                    result = y.ptr,
                    ri = yi,
                    rj = yj,
                    axis = axis,
                    start = indices.first,
                    end = indices.last,
                    step = indices.step,
                    runtime = runtime,
                )
            }

            else -> KotlinBackend.copyInto(x = x, y, indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is GPUJvmBuffer -> {
                JShape.copyIntoD3(
                    x = x.toGPUBuffer().ptr,
                    result = y.ptr,
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

            else -> KotlinBackend.copyInto(x = x, y, indices)
        }
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        val result = GPUJvmBuffer.create(i * x.size * k)
        JIndex.gather(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            i = i,
            j = j,
            k = k,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        val result = GPUJvmBuffer.create(i * j * k)
        JIndex.scatterAdd(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            i = i,
            j = j,
            k = k,
            b = b,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.greaterThanD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.greaterThanD1ToD1(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.lessThanD1ToD0(x = x.toGPUBuffer().ptr, y = y, result = result.ptr, runtime = runtime)
        return result
    }

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.lessThanD1ToD1(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.equalsD1ToD0(
            x = x.toGPUBuffer().ptr,
            y = y,
            absoluteTolerance = absoluteTolerance,
            relativeTolerance = relativeTolerance,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JCompare.equalsD1ToD1(
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            absoluteTolerance = absoluteTolerance,
            relativeTolerance = relativeTolerance,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(condition.size)
        JWhere.whereD0ToD0(
            condition = condition.toGPUBuffer().ptr,
            x = x,
            y = y,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(y.size)
        JWhere.whereD0ToD1(
            condition = condition.toGPUBuffer().ptr,
            x = x,
            y = y.toGPUBuffer().ptr,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JWhere.whereD1ToD0(
            condition = condition.toGPUBuffer().ptr,
            x = x.toGPUBuffer().ptr,
            y = y,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JWhere.whereD1ToD1(
            condition = condition.toGPUBuffer().ptr,
            x = x.toGPUBuffer().ptr,
            y = y.toGPUBuffer().ptr,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = GPUJvmBuffer.create(x.size)
        JShape.flipD3(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            axis = axis,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val rj = (xj - windowSize + padding * 2) / stride + 1
        val result = GPUJvmBuffer.create(b * xi * rj * window)
        JShape.unfoldD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val rj = (xj - windowSize + padding * 2) / stride + 1
        val rk = (xk - windowSize + padding * 2) / stride + 1
        val result = GPUJvmBuffer.create(b * xi * rj * rk * window * window)
        JShape.unfoldD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            window = window,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (xk - 1) * dilation + 1
        val nj = windowSize + (xj - 1) * stride - padding * 2
        val result = GPUJvmBuffer.create(b * xi * nj)
        JShape.foldD1(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val window = kotlin.math.sqrt(xl.toDouble()).toInt()
        val windowSize = (window - 1) * dilation + 1
        val nj = windowSize + (xj - 1) * stride - padding * 2
        val nk = windowSize + (xk - 1) * stride - padding * 2
        val result = GPUJvmBuffer.create(b * xi * nj * nk)
        JShape.foldD2(
            x = x.toGPUBuffer().ptr,
            xi = xi,
            xj = xj,
            xk = xk,
            xl = xl,
            b = b,
            stride = stride,
            dilation = dilation,
            padding = padding,
            result = result.ptr,
            runtime = runtime,
        )
        return result
    }

    override fun flush() = JRuntime.flush(runtime)
    override fun sync() = JRuntime.sync(runtime)

    private fun GPUJvmBuffer.Companion.create(size: Int) = GPUJvmBuffer.create(
        size = size,
        runtime = runtime,
    )

    private fun DataBuffer.toGPUBuffer(): GPUJvmBuffer = toGPUBuffer(runtime = runtime)
}

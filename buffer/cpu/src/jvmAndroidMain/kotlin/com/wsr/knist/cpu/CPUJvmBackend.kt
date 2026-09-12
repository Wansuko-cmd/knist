package com.wsr.knist.cpu

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.KotlinBackend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.size
import com.wsr.knist.cpu.elementwise.compare.JCompare
import com.wsr.knist.cpu.elementwise.compare.where.JWhere
import com.wsr.knist.cpu.elementwise.generator.JGenerator
import com.wsr.knist.cpu.elementwise.math.JMath
import com.wsr.knist.cpu.elementwise.operation.div.JDiv
import com.wsr.knist.cpu.elementwise.operation.minus.JMinus
import com.wsr.knist.cpu.elementwise.operation.plus.JPlus
import com.wsr.knist.cpu.elementwise.operation.times.JTimes
import com.wsr.knist.cpu.index.JIndex
import com.wsr.knist.cpu.linalg.JMatMul
import com.wsr.knist.cpu.reduction.JReduction
import com.wsr.knist.cpu.shape.JShape
import kotlin.math.min
import kotlin.random.Random

class CPUJvmBackend(fallback: IBackend, maxPoolBytes: Long) : IBackend by fallback {
    private val runtime = JRuntime.allocate(poolSize = maxPoolBytes)
    override val generator = CPUJvmBuffer.createGenerator(runtime = runtime)

    // 0次元
    override fun plus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JPlus.plusD0ToD1(x, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    // 1次元
    override fun plus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JPlus.plusD1ToD2(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, yi, yj, axis, result.ptr)
        return result
    }

    override fun plus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JPlus.plusD1ToD3(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis,
            result.ptr,
        )
        return result
    }

    // 2次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD2ToD1(x.toCPUBuffer().ptr, xi, xj, y.toCPUBuffer().ptr, axis, result.ptr)
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JPlus.plusD2ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    // 3次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD3ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD3ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JPlus.plusD3ToD4(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 4次元
    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun plus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JPlus.plusD4ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 0次元
    override fun minus(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JMinus.minusD0ToD1(x, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    // 1次元
    override fun minus(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JMinus.minusD1ToD2(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, yi, yj, axis, result.ptr)
        return result
    }

    override fun minus(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JMinus.minusD1ToD3(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis,
            result.ptr,
        )
        return result
    }

    // 2次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD2ToD1(x.toCPUBuffer().ptr, xi, xj, y.toCPUBuffer().ptr, axis, result.ptr)
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JMinus.minusD2ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    // 3次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD3ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD3ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JMinus.minusD3ToD4(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 4次元
    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun minus(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMinus.minusD4ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 0次元
    override fun times(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JTimes.timesD0ToD1(x, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    // 1次元
    override fun times(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JTimes.timesD1ToD2(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, yi, yj, axis, result.ptr)
        return result
    }

    override fun times(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JTimes.timesD1ToD3(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis,
            result.ptr,
        )
        return result
    }

    // 2次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD2ToD1(x.toCPUBuffer().ptr, xi, xj, y.toCPUBuffer().ptr, axis, result.ptr)
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JTimes.timesD2ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    // 3次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD3ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD3ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JTimes.timesD3ToD4(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 4次元
    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun times(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JTimes.timesD4ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 0次元
    override fun div(x: Float, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JDiv.divD0ToD1(x, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    // 1次元
    override fun div(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JDiv.divD1ToD2(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, yi, yj, axis, result.ptr)
        return result
    }

    override fun div(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JDiv.divD1ToD3(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis,
            result.ptr,
        )
        return result
    }

    // 2次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD2ToD1(x.toCPUBuffer().ptr, xi, xj, y.toCPUBuffer().ptr, axis, result.ptr)
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JDiv.divD2ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    // 3次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD3ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD3ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, yl: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JDiv.divD3ToD4(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            yl,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    // 4次元
    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD4ToD1(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            axis,
            result.ptr,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD4ToD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            axis1,
            axis2,
            result.ptr,
        )
        return result
    }

    override fun div(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, axis3: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JDiv.divD4ToD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            y.toCPUBuffer().ptr,
            yi,
            yj,
            yk,
            axis1,
            axis2,
            axis3,
            result.ptr,
        )
        return result
    }

    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer {
        val result = CPUJvmBuffer.create(b)
        JMatMul.inner(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            b,
            result.ptr,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer {
        val result = CPUJvmBuffer.create(m)
        JMatMul.matMulD2ToD1(
            x.toCPUBuffer().ptr,
            transX,
            y.toCPUBuffer().ptr,
            m,
            k,
            result.ptr,
        )
        return result
    }

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer {
        val result = CPUJvmBuffer.create(n)
        JMatMul.matMulD1ToD2(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            transY,
            n,
            k,
            result.ptr,
        )
        return result
    }

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, transY: Boolean, m: Int, n: Int, k: Int, b: Int): DataBuffer {
        val result = CPUJvmBuffer.create(b * m * n)
        JMatMul.matMulD2ToD2(
            x.toCPUBuffer().ptr,
            transX,
            y.toCPUBuffer().ptr,
            transY,
            m,
            n,
            k,
            b,
            result.ptr,
        )
        return result
    }

    override fun exp(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMath.exp(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun ln(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMath.ln(x.toCPUBuffer().ptr, e, result.ptr)
        return result
    }

    override fun sigmoid(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMath.sigmoid(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun pow(x: DataBuffer, n: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMath.pow(x.toCPUBuffer().ptr, n, result.ptr)
        return result
    }

    override fun sqrt(x: DataBuffer, e: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JMath.sqrt(x.toCPUBuffer().ptr, e, result.ptr)
        return result
    }

    override fun random(size: Int, from: Float, until: Float, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(size)
        JGenerator.random(from, until, random.nextLong(), result.ptr)
        return result
    }

    override fun average(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.averageD1(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.averageD2(x.toCPUBuffer().ptr, xi, xj, axis, result.ptr)
        return result
    }

    override fun average(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.averageD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    override fun max(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.maxD1(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.maxD2(x.toCPUBuffer().ptr, xi, xj, axis, result.ptr)
        return result
    }

    override fun max(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.maxD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    override fun min(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.minD1(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.minD2(x.toCPUBuffer().ptr, xi, xj, axis, result.ptr)
        return result
    }

    override fun min(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.minD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    override fun sum(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.sumD1(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.sumD2(x.toCPUBuffer().ptr, xi, xj, axis, result.ptr)
        return result
    }

    override fun sum(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.sumD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    override fun maxIndex(x: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.maxIndexD1(x.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.maxIndexD2(x.toCPUBuffer().ptr, xi, xj, axis, result.ptr)
        return result
    }

    override fun maxIndex(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.maxIndexD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    override fun topK(x: DataBuffer, k: Int, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.topKD1(x.toCPUBuffer().ptr, k, random.nextLong(), result.ptr)
        return result
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.topKD2(x.toCPUBuffer().ptr, xi, xj, k, axis, random.nextLong(), result.ptr)
        return result
    }

    override fun topK(x: DataBuffer, xi: Int, xj: Int, xk: Int, k: Int, axis: Int, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.topKD3(x.toCPUBuffer().ptr, xi, xj, xk, k, axis, random.nextLong(), result.ptr)
        return result
    }

    override fun topP(x: DataBuffer, p: Float, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(1)
        JReduction.topPD1(x.toCPUBuffer().ptr, p, random.nextLong(), result.ptr)
        return result
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj
                else -> xi
            },
        )
        JReduction.topPD2(x.toCPUBuffer().ptr, xi, xj, p, axis, random.nextLong(), result.ptr)
        return result
    }

    override fun topP(x: DataBuffer, xi: Int, xj: Int, xk: Int, p: Float, axis: Int, random: Random): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> xj * xk
                1 -> xi * xk
                else -> xi * xj
            },
        )
        JReduction.topPD3(x.toCPUBuffer().ptr, xi, xj, xk, p, axis, random.nextLong(), result.ptr)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JShape.transposeD2(x.toCPUBuffer().ptr, xi, xj, result.ptr)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, axisI: Int, axisJ: Int, axisK: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JShape.transposeD3(x.toCPUBuffer().ptr, xi, xj, xk, axisI, axisJ, axisK, result.ptr)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JShape.transposeD4(x.toCPUBuffer().ptr, xi, xj, xk, xl, axisI, axisJ, axisK, axisL, result.ptr)
        return result
    }

    override fun transpose(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, xm: Int, axisI: Int, axisJ: Int, axisK: Int, axisL: Int, axisM: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JShape.transposeD5(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            xl,
            xm,
            axisI,
            axisJ,
            axisK,
            axisL,
            axisM,
            result.ptr,
        )
        return result
    }

    override fun slice(x: DataBuffer, indices: IntProgression): DataBuffer {
        val result = CPUJvmBuffer.create(min(x.size, indices.size))
        JShape.sliceD1(x.toCPUBuffer().ptr, indices.first, indices.last, indices.step, result.ptr)
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> min(xi, indices.size) * xj
                else -> xi * min(xj, indices.size)
            },
        )
        JShape.sliceD2(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            axis,
            indices.first,
            indices.last,
            indices.step,
            result.ptr,
        )
        return result
    }

    override fun slice(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int, indices: IntProgression): DataBuffer {
        val result = CPUJvmBuffer.create(
            size = when (axis) {
                0 -> min(xi, indices.size) * xj * xk
                1 -> xi * min(xj, indices.size) * xk
                else -> xi * xj * min(xk, indices.size)
            },
        )
        JShape.sliceD3(
            x.toCPUBuffer().ptr,
            xi,
            xj,
            xk,
            axis,
            indices.first,
            indices.last,
            indices.step,
            result.ptr,
        )
        return result
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, indices: IntProgression) {
        when (y) {
            is CPUJvmBuffer -> {
                JShape.copyIntoD1(
                    x.toCPUBuffer().ptr,
                    y.ptr,
                    indices.first,
                    indices.last,
                    indices.step,
                )
            }

            else -> KotlinBackend.copyInto(x, y, indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is CPUJvmBuffer -> {
                JShape.copyIntoD2(
                    x.toCPUBuffer().ptr,
                    y.ptr,
                    yi,
                    yj,
                    axis,
                    indices.first,
                    indices.last,
                    indices.step,
                )
            }

            else -> KotlinBackend.copyInto(x, y, yi, yj, axis, indices)
        }
    }

    override fun copyInto(x: DataBuffer, y: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, indices: IntProgression) {
        when (y) {
            is CPUJvmBuffer -> {
                JShape.copyIntoD3(
                    x.toCPUBuffer().ptr,
                    y.ptr,
                    yi,
                    yj,
                    yk,
                    axis,
                    indices.first,
                    indices.last,
                    indices.step,
                )
            }

            else -> KotlinBackend.copyInto(x, y, yi, yj, yk, axis, indices)
        }
    }

    override fun gather(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int): DataBuffer {
        val result = CPUJvmBuffer.create(i * x.size * k)
        JIndex.gather(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, i, j, k, result.ptr)
        return result
    }

    override fun scatterAdd(x: DataBuffer, y: DataBuffer, i: Int, j: Int, k: Int, b: Int): DataBuffer {
        val result = CPUJvmBuffer.create(i * j * k)
        JIndex.scatterAdd(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, i, j, k, b, result.ptr)
        return result
    }

    override fun greaterThan(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.greaterThanD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun greaterThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.greaterThanD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun lessThan(x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.lessThanD1ToD0(x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun lessThan(x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.lessThanD1ToD1(x.toCPUBuffer().ptr, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun equals(x: DataBuffer, y: Float, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.equalsD1ToD0(x.toCPUBuffer().ptr, y, absoluteTolerance, relativeTolerance, result.ptr)
        return result
    }

    override fun equals(x: DataBuffer, y: DataBuffer, absoluteTolerance: Float, relativeTolerance: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JCompare.equalsD1ToD1(
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            absoluteTolerance,
            relativeTolerance,
            result.ptr,
        )
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(condition.size)
        JWhere.whereD0ToD0(condition.toCPUBuffer().ptr, x, y, result.ptr)
        return result
    }

    override fun where(condition: DataBuffer, x: Float, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(y.size)
        JWhere.whereD0ToD1(condition.toCPUBuffer().ptr, x, y.toCPUBuffer().ptr, result.ptr)
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: Float): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JWhere.whereD1ToD0(condition.toCPUBuffer().ptr, x.toCPUBuffer().ptr, y, result.ptr)
        return result
    }

    override fun where(condition: DataBuffer, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JWhere.whereD1ToD1(
            condition.toCPUBuffer().ptr,
            x.toCPUBuffer().ptr,
            y.toCPUBuffer().ptr,
            result.ptr,
        )
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj + padding * 2 - windowSize) / stride + 1
        val result = CPUJvmBuffer.create(b * xi * oj * window)
        JShape.unfoldD1(x.toCPUBuffer().ptr, xi, xj, b, window, stride, dilation, padding, result.ptr)
        return result
    }

    override fun unfold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, window: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (window - 1) * dilation + 1
        val oj = (xj + padding * 2 - windowSize) / stride + 1
        val ok = (xk + padding * 2 - windowSize) / stride + 1
        val ww = window * window
        val result = CPUJvmBuffer.create(b * xi * oj * ok * ww)
        JShape.unfoldD2(x.toCPUBuffer().ptr, xi, xj, xk, b, window, stride, dilation, padding, result.ptr)
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val windowSize = (xk - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val result = CPUJvmBuffer.create(b * xi * oj)
        JShape.foldD1(x.toCPUBuffer().ptr, xi, xj, xk, b, stride, dilation, padding, result.ptr)
        return result
    }

    override fun fold(x: DataBuffer, xi: Int, xj: Int, xk: Int, xl: Int, b: Int, stride: Int, dilation: Int, padding: Int): DataBuffer {
        val window = kotlin.math.sqrt(xl.toDouble()).toInt()
        val windowSize = (window - 1) * dilation + 1
        val oj = windowSize + (xj - 1) * stride - padding * 2
        val ok = windowSize + (xk - 1) * stride - padding * 2
        val result = CPUJvmBuffer.create(b * xi * oj * ok)
        JShape.foldD2(x.toCPUBuffer().ptr, xi, xj, xk, xl, b, stride, dilation, padding, result.ptr)
        return result
    }

    override fun flip(x: DataBuffer, xi: Int, xj: Int, xk: Int, axis: Int): DataBuffer {
        val result = CPUJvmBuffer.create(x.size)
        JShape.flipD3(x.toCPUBuffer().ptr, xi, xj, xk, axis, result.ptr)
        return result
    }

    private fun CPUJvmBuffer.Companion.create(size: Int) = CPUJvmBuffer.create(
        size = size,
        runtime = runtime,
    )

    private fun DataBuffer.toCPUBuffer(): CPUJvmBuffer = toCPUBuffer(runtime = runtime)
}

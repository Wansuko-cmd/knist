package com.wsr.optimizer

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.collecction.average.batchAverage
import com.wsr.core.IOType
import com.wsr.core.collection.sum.sum
import com.wsr.core.math.pow
import com.wsr.core.operation.times.times
import kotlin.math.sqrt
import kotlinx.serialization.Serializable

interface Optimizer {
    fun d1(size: Int): D1
    fun d2(x: Int, y: Int): D2
    fun d3(x: Int, y: Int, z: Int): D3

    @Serializable
    abstract class D1(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var _step: Int = 0
        protected val step: Int get() = _step / _stepUnit
        protected abstract fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1

        fun adapt(weight: IOType.D1, dw: IOType.D1, enableClip: Boolean = true): IOType.D1 = adapt(
            weight = weight,
            dw = batchOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D1, dw: Batch<IOType.D1>, enableClip: Boolean = true): IOType.D1 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled).also { _step++ }
        }
    }

    @Serializable
    abstract class D2(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var _step: Int = 0
        protected val step: Int get() = _step / _stepUnit
        protected abstract fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2

        fun adapt(weight: IOType.D2, dw: IOType.D2, enableClip: Boolean = true): IOType.D2 = adapt(
            weight = weight,
            dw = batchOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D2, dw: Batch<IOType.D2>, enableClip: Boolean = true): IOType.D2 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled).also { _step++ }
        }
    }

    @Serializable
    abstract class D3(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var _step: Int = 0
        protected val step: Int get() = _step / _stepUnit
        protected abstract fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3

        fun adapt(weight: IOType.D3, dw: IOType.D3, enableClip: Boolean = true): IOType.D3 = adapt(
            weight = weight,
            dw = batchOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D3, dw: Batch<IOType.D3>, enableClip: Boolean = true): IOType.D3 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled).also { _step++ }
        }
    }
}

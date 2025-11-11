package com.wsr.optimizer

import com.wsr.IOType
import com.wsr.collection.batchAverage
import com.wsr.collection.sum
import com.wsr.operator.times
import com.wsr.power.pow
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

interface Optimizer {
    fun d1(size: Int): D1
    fun d2(x: Int, y: Int): D2
    fun d3(x: Int, y: Int, z: Int): D3

    @Serializable
    abstract class D1(private val _maxNorm: Double = Double.MAX_VALUE) {
        protected abstract fun adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1

        fun adapt(weight: IOType.D1, dw: IOType.D1, enableClip: Boolean = true): IOType.D1 = adapt(
            weight = weight,
            dw = listOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D1, dw: List<IOType.D1>, enableClip: Boolean = true): IOType.D1 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled)
        }
    }

    @Serializable
    abstract class D2(private val _maxNorm: Double = Double.MAX_VALUE) {
        protected abstract fun adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2

        fun adapt(weight: IOType.D2, dw: IOType.D2, enableClip: Boolean = true): IOType.D2 = adapt(
            weight = weight,
            dw = listOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D2, dw: List<IOType.D2>, enableClip: Boolean = true): IOType.D2 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled)
        }
    }

    @Serializable
    abstract class D3(private val _maxNorm: Double = Double.MAX_VALUE) {
        protected abstract fun adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3

        fun adapt(weight: IOType.D3, dw: IOType.D3, enableClip: Boolean = true): IOType.D3 = adapt(
            weight = weight,
            dw = listOf(dw),
            enableClip = enableClip,
        )

        fun adapt(weight: IOType.D3, dw: List<IOType.D3>, enableClip: Boolean = true): IOType.D3 {
            val dw = dw.batchAverage()
            val norm = sqrt(dw.pow(2).sum())
            val scaled = if (norm > _maxNorm && enableClip) {
                val scale = _maxNorm / norm
                dw * scale
            } else {
                dw
            }
            return adapt(weight, scaled)
        }
    }
}

package com.wsr.knist.network.optimizer

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.elementwise.operation.div.div
import com.wsr.knist.core.elementwise.operation.plus.plus
import kotlinx.serialization.Serializable

interface Optimizer {
    fun d1(i: Int): D1
    fun d2(i: Int, j: Int): D2
    fun d3(i: Int, j: Int, k: Int): D3
    fun d4(i: Int, j: Int, k: Int, l: Int): D4

    @Serializable
    abstract class D1(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var acc: IOType.D1.Global? = null
        private var pending: Int = 0
        protected var step: Int = 0
            private set

        protected abstract fun IOScope.adapt(weight: IOType.D1, dw: IOType.D1): IOType.D1

        context(scope: IOScope)
        open fun adapt(weight: IOType.D1, dw: IOType.D1, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D1 {
            acc = (if (acc == null) dw else acc!! + dw).toGlobal()
            if (++pending % _stepUnit != 0) return weight

            val avg = acc!! / _stepUnit.toFloat()
            acc = null
            pending = 0

            if (enableClip) {
                with(scope) {
                    val norm = avg.pow(2).sum().sqrt()
                    val scale = _maxNorm / norm
                    val clipped = scale.where(condition = scale lt 1f, onFalse = 1f)
                    return adapt(weight, avg * clipped).also { step++ }
                }
            }
            return with(scope) { adapt(weight, avg) }.also { step++ }
        }

        context(scope: IOScope)
        open fun adapt(weight: IOType.D1, dw: Batch<IOType.D1>, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D1 = with(scope) { adapt(weight, dw.batchAverage(), enableClip) }
    }

    @Serializable
    abstract class D2(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var acc: IOType.D2.Global? = null
        private var pending: Int = 0
        protected var step: Int = 0
            private set

        protected abstract fun IOScope.adapt(weight: IOType.D2, dw: IOType.D2): IOType.D2

        context(scope: IOScope)
        open fun adapt(weight: IOType.D2, dw: IOType.D2, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D2 {
            acc = (if (acc == null) dw else acc!! + dw).toGlobal()
            if (++pending % _stepUnit != 0) return weight

            val avg = acc!! / _stepUnit.toFloat()
            acc = null
            pending = 0

            if (enableClip) {
                with(scope) {
                    val norm = avg.pow(2).sum().sqrt()
                    val scale = _maxNorm / norm
                    val clipped = scale.where(condition = scale lt 1f, onFalse = 1f)
                    return adapt(weight, avg * clipped).also { step++ }
                }
            }
            return with(scope) { adapt(weight, avg) }.also { step++ }
        }

        context(scope: IOScope)
        open fun adapt(weight: IOType.D2, dw: Batch<IOType.D2>, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D2 = with(scope) { adapt(weight, dw.batchAverage(), enableClip) }
    }

    @Serializable
    abstract class D3(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var acc: IOType.D3.Global? = null
        private var pending: Int = 0
        protected var step: Int = 0
            private set

        protected abstract fun IOScope.adapt(weight: IOType.D3, dw: IOType.D3): IOType.D3

        context(scope: IOScope)
        open fun adapt(weight: IOType.D3, dw: IOType.D3, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D3 {
            acc = (if (acc == null) dw else acc!! + dw).toGlobal()
            if (++pending % _stepUnit != 0) return weight

            val avg = acc!! / _stepUnit.toFloat()
            acc = null
            pending = 0

            if (enableClip) {
                with(scope) {
                    val norm = avg.pow(2).sum().sqrt()
                    val scale = _maxNorm / norm
                    val clipped = scale.where(condition = scale lt 1f, onFalse = 1f)
                    return adapt(weight, avg * clipped).also { step++ }
                }
            }
            return with(scope) { adapt(weight, avg) }.also { step++ }
        }

        context(scope: IOScope)
        open fun adapt(weight: IOType.D3, dw: Batch<IOType.D3>, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D3 = with(scope) { adapt(weight, dw.batchAverage(), enableClip) }
    }

    @Serializable
    abstract class D4(private val _maxNorm: Float = Float.MAX_VALUE, private val _stepUnit: Int = 1) {
        private var acc: IOType.D4.Global? = null
        private var pending: Int = 0
        protected var step: Int = 0
            private set

        protected abstract fun IOScope.adapt(weight: IOType.D4, dw: IOType.D4): IOType.D4

        context(scope: IOScope)
        open fun adapt(weight: IOType.D4, dw: IOType.D4, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D4 {
            acc = (if (acc == null) dw else acc!! + dw).toGlobal()
            if (++pending % _stepUnit != 0) return weight

            val avg = acc!! / _stepUnit.toFloat()
            acc = null
            pending = 0

            if (enableClip) {
                with(scope) {
                    val norm = avg.pow(2).sum().sqrt()
                    val scale = _maxNorm / norm
                    val clipped = scale.where(condition = scale lt 1f, onFalse = 1f)
                    return adapt(weight, avg * clipped).also { step++ }
                }
            }
            return with(scope) { adapt(weight, avg) }.also { step++ }
        }

        context(scope: IOScope)
        open fun adapt(weight: IOType.D4, dw: Batch<IOType.D4>, enableClip: Boolean = _maxNorm != Float.MAX_VALUE): IOType.D4 = with(scope) { adapt(weight, dw.batchAverage(), enableClip) }
    }
}

package com.wsr.reshape

import com.wsr.IOType
import com.wsr.Layer
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
sealed interface Reshape : Layer {
    @Serializable
    abstract class D2ToD1 : Reshape {
        abstract val outputSize: Int
        protected abstract fun expect(input: List<IOType.D2>): List<IOType.D1>
        protected abstract fun train(
            input: List<IOType.D2>,
            calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
        ): List<IOType.D2>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D2>)
        final override fun _train(
            input: List<IOType>,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> =
            train(
                input = input as List<IOType.D2>,
                calcDelta = { input: List<IOType.D1> -> calcDelta(input) as List<IOType.D1> },
            )
    }
}

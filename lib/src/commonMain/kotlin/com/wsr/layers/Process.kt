package com.wsr.layers

import com.wsr.IOType
import com.wsr.Layer
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
sealed interface Process : Layer {
    @Serializable
    abstract class D1 : Process {
        abstract val outputSize: Int

        protected abstract fun expect(input: List<IOType.D1>): List<IOType.D1>
        protected abstract fun train(
            input: List<IOType.D1>,
            calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
        ): List<IOType.D1>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D1>)
        final override fun _train(
            input: List<IOType>,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> =
            train(
                input = input as List<IOType.D1>,
                calcDelta = { input: List<IOType.D1> -> calcDelta(input) as List<IOType.D1> }
            )
    }

    @Serializable
    abstract class D2 : Process {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: List<IOType.D2>): List<IOType.D2>
        protected abstract fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D2>)
        final override fun _train(
            input: List<IOType>,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> =
            train(
                input = input as List<IOType.D2>,
                calcDelta = { input: List<IOType.D2> -> calcDelta(input) as List<IOType.D2> },
            )
    }

    @Serializable
    abstract class Reshape : Process {
        protected abstract fun expect(input: List<IOType>): List<IOType>
        protected abstract fun train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input)
        final override fun _train(
            input: List<IOType>,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> = train(input, calcDelta)
    }
}

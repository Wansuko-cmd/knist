package com.wsr.layers

import com.wsr.common.IOType
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
sealed interface Layer {
    fun expect(input: IOType): IOType
    fun train(input: IOType, calcDelta: (IOType) -> IOType): IOType

    fun expect(input: List<IOType>): List<IOType>
    fun train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType>

    @Serializable
    abstract class D1 : Layer {
        abstract val outputSize: Int

        protected abstract fun expect(input: IOType.D1): IOType.D1
        protected abstract fun train(input: IOType.D1, calcDelta: (IOType.D1) -> IOType.D1): IOType.D1

        protected abstract fun expectD1(input: List<IOType.D1>): List<IOType.D1>
        protected abstract fun trainD1(
            input: List<IOType.D1>,
            calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
        ): List<IOType.D1>

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D1)

        override fun train(input: IOType, calcDelta: (IOType) -> IOType): IOType =
            train(
                input = input as IOType.D1,
                calcDelta = { input: IOType.D1 -> calcDelta(input) as IOType.D1 },
            )

        override fun expect(input: List<IOType>): List<IOType> = expectD1(input = input as List<IOType.D1>)
        override fun train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType> =
            trainD1(
                input = input as List<IOType.D1>,
                calcDelta = { input: List<IOType.D1> -> calcDelta(input) as List<IOType.D1> }
            )
    }

    @Serializable
    abstract class D2 : Layer {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: IOType.D2): IOType.D2
        protected abstract fun train(input: IOType.D2, calcDelta: (IOType.D2) -> IOType.D2): IOType.D2

        protected abstract fun expectD2(input: List<IOType.D2>): List<IOType.D2>
        protected abstract fun trainD2(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2>

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D2)
        override fun train(input: IOType, calcDelta: (IOType) -> IOType): IOType =
            train(
                input = input as IOType.D2,
                calcDelta = { input: IOType.D2 -> calcDelta(input) as IOType.D2 },
            )

        override fun expect(input: List<IOType>): List<IOType> = expectD2(input = input as List<IOType.D2>)
        override fun train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType> =
            trainD2(
                input = input as List<IOType.D2>,
                calcDelta = { input: List<IOType.D2> -> calcDelta(input) as List<IOType.D2> },
            )
    }

    @Serializable
    abstract class Reshape : Layer
}

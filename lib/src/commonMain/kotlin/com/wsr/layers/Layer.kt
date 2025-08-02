package com.wsr.layers

import com.wsr.common.IOType
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
sealed interface Layer {
    fun expect(input: IOType): IOType
    fun train(input: IOType, delta: (IOType) -> IOType): IOType

    @Serializable
    abstract class D1 : Layer {
        abstract val outputSize: Int

        protected abstract fun expect(input: IOType.D1): IOType.D1
        protected abstract fun train(input: IOType.D1, delta: (IOType.D1) -> IOType.D1): IOType.D1

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D1)

        override fun train(input: IOType, delta: (IOType) -> IOType): IOType =
            train(
                input = input as IOType.D1,
                delta = { input: IOType.D1 -> delta(input) as IOType.D1 },
            )
    }

    @Serializable
    abstract class D2 : Layer {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: IOType.D2): IOType.D2
        protected abstract fun train(input: IOType.D2, delta: (IOType.D2) -> IOType.D2): IOType.D2

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D2)
        override fun train(input: IOType, delta: (IOType) -> IOType): IOType =
            train(
                input = input as IOType.D2,
                delta = { input: IOType.D2 -> delta(input) as IOType.D2 },
            )
    }

    @Serializable
    abstract class Reshape : Layer
}

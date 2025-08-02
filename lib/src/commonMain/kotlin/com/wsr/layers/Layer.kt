package com.wsr.layers

import com.wsr.common.IOType
import kotlinx.serialization.Serializable

@Serializable
sealed interface Layer {
    val inputShape: List<Int>
    val outputShape: List<Int>
    fun expect(input: IOType): IOType
    fun train(input: IOType, delta: (IOType) -> IOType): IOType

    @Serializable
    abstract class D1 : Layer {
        abstract val numOfInput: Int
        abstract val numOfOutput: Int
        override val inputShape: List<Int> = listOf(numOfInput)
        override val outputShape: List<Int> = listOf(numOfOutput)

        protected abstract fun expect(input: IOType.D1): IOType.D1
        protected abstract fun train(input: IOType.D1, delta: (IOType.D1) -> IOType.D1): IOType.D1

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D1)
        @Suppress("UNCHECKED_CAST")
        override fun train(input: IOType, delta: (IOType) -> IOType): IOType =
            train(
                input = input.toD1(),
                delta = { input: IOType.D1 -> delta(input) as IOType.D1 },
            )
    }

    @Serializable
    abstract class D2 : Layer {
        protected abstract fun expect(input: IOType.D2): IOType.D2
        protected abstract fun train(input: IOType.D2, delta: (IOType.D2) -> IOType.D2): IOType.D2

        override fun expect(input: IOType): IOType = expect(input = input as IOType.D2)
        override fun train(input: IOType, delta: (IOType) -> IOType): IOType =
            train(
                input = input as IOType.D2,
                delta = { input: IOType.D2 -> delta(input) as IOType.D2 },
            )
    }
}

package com.wsr.layers

import com.wsr.common.IOTypeD1
import kotlinx.serialization.Serializable

@Serializable
sealed interface Layer<T> {
    val inputShape: List<Int>
    val outputShape: List<Int>
    fun expect(input: T): T
    fun train(input: T, delta: (T) -> T) : T

    @Serializable
    abstract class D1 : Layer<IOTypeD1> {
        abstract val numOfInput: Int
        abstract val numOfOutput: Int
        override val inputShape: List<Int> = listOf(numOfInput)
        override val outputShape: List<Int> = listOf(numOfOutput)
    }
}

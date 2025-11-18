package com.wsr.output

import com.wsr.IOType
import kotlinx.serialization.Serializable

data class TResult<T : IOType>(
    val loss: Float,
    val delta: List<T>,
)

@Suppress("UNCHECKED_CAST")
sealed interface Output {
    @Suppress("FunctionName")
    fun _expect(input: List<IOType>): List<IOType>

    @Suppress("FunctionName")
    fun _train(input: List<IOType>, label: List<IOType>): TResult<*>

    @Serializable
    abstract class D1 : Output {
        protected abstract fun expect(input: List<IOType.D1>): List<IOType.D1>

        protected abstract fun train(input: List<IOType.D1>, label: List<IOType.D1>): TResult<IOType.D1>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D1>)

        final override fun _train(input: List<IOType>, label: List<IOType>): TResult<*> = train(
            input = input as List<IOType.D1>,
            label = label as List<IOType.D1>,
        )
    }

    @Serializable
    abstract class D2 : Output {
        protected abstract fun expect(input: List<IOType.D2>): List<IOType.D2>

        protected abstract fun train(input: List<IOType.D2>, label: List<IOType.D2>): TResult<IOType.D2>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D2>)

        final override fun _train(input: List<IOType>, label: List<IOType>): TResult<*> = train(
            input = input as List<IOType.D2>,
            label = label as List<IOType.D2>,
        )
    }
}

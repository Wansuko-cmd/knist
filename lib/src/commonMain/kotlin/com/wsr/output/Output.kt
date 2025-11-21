package com.wsr.output

import com.wsr.Batch
import com.wsr.IOType
import kotlinx.serialization.Serializable

data class TResult<T : IOType>(val loss: Float, val delta: Batch<T>)

@Suppress("UNCHECKED_CAST")
sealed interface Output {
    @Suppress("FunctionName")
    fun _expect(input: Batch<IOType>): Batch<IOType>

    @Suppress("FunctionName")
    fun _train(input: Batch<IOType>, label: Batch<IOType>): TResult<*>

    @Serializable
    abstract class D1 : Output {
        protected abstract fun expect(input: Batch<IOType.D1>): Batch<IOType.D1>

        protected abstract fun train(input: Batch<IOType.D1>, label: Batch<IOType.D1>): TResult<IOType.D1>

        final override fun _expect(input: Batch<IOType>): Batch<IOType> = expect(input = input as Batch<IOType.D1>)

        final override fun _train(input: Batch<IOType>, label: Batch<IOType>): TResult<*> = train(
            input = input as Batch<IOType.D1>,
            label = label as Batch<IOType.D1>,
        )
    }

    @Serializable
    abstract class D2 : Output {
        protected abstract fun expect(input: Batch<IOType.D2>): Batch<IOType.D2>

        protected abstract fun train(input: Batch<IOType.D2>, label: Batch<IOType.D2>): TResult<IOType.D2>

        final override fun _expect(input: Batch<IOType>): Batch<IOType> = expect(input = input as Batch<IOType.D2>)

        final override fun _train(input: Batch<IOType>, label: Batch<IOType>): TResult<*> = train(
            input = input as Batch<IOType.D2>,
            label = label as Batch<IOType.D2>,
        )
    }
}

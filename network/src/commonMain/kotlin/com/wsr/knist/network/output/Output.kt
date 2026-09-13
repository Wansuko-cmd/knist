package com.wsr.knist.network.output

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import kotlinx.serialization.Serializable

data class TResult<T : IOType>(val loss: IOType.D0, val delta: Batch<T>)

@Suppress("UNCHECKED_CAST")
sealed interface Output {
    @Suppress("FunctionName")
    fun IOScope._expect(input: Batch<IOType>): Batch<IOType>

    @Suppress("FunctionName")
    fun IOScope._train(input: Batch<IOType>, label: (output: Batch<IOType>) -> Batch<IOType>): TResult<*>

    @Serializable
    abstract class D1 : Output {
        protected abstract fun IOScope.expect(input: Batch<IOType.D1>): Batch<IOType.D1>

        protected abstract fun IOScope.train(input: Batch<IOType.D1>, label: (Batch<IOType.D1>) -> Batch<IOType.D1>): TResult<IOType.D1>

        final override fun IOScope._expect(input: Batch<IOType>): Batch<IOType> = expect(input = input as Batch<IOType.D1>)

        final override fun IOScope._train(input: Batch<IOType>, label: (Batch<IOType>) -> Batch<IOType>): TResult<*> = train(
            input = input as Batch<IOType.D1>,
            label = label as (Batch<IOType.D1>) -> Batch<IOType.D1>,
        )
    }

    @Serializable
    abstract class D2 : Output {
        protected abstract fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2>

        protected abstract fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2>

        final override fun IOScope._expect(input: Batch<IOType>): Batch<IOType> = expect(input = input as Batch<IOType.D2>)

        final override fun IOScope._train(input: Batch<IOType>, label: (Batch<IOType>) -> Batch<IOType>): TResult<*> = train(
            input = input as Batch<IOType.D2>,
            label = label as (Batch<IOType.D2>) -> Batch<IOType.D2>,
        )
    }

    @Serializable
    abstract class D3 : Output {
        protected abstract fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3>

        protected abstract fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3>

        final override fun IOScope._expect(input: Batch<IOType>): Batch<IOType> = expect(input = input as Batch<IOType.D3>)

        final override fun IOScope._train(input: Batch<IOType>, label: (Batch<IOType>) -> Batch<IOType>): TResult<*> = train(
            input = input as Batch<IOType.D3>,
            label = label as (Batch<IOType.D3>) -> Batch<IOType.D3>,
        )
    }
}

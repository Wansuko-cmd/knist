package com.wsr.output

import com.wsr.IOType
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
sealed interface Output {
    @Suppress("FunctionName")
    fun _expect(input: List<IOType>): List<IOType>

    @Suppress("FunctionName")
    fun _train(input: List<IOType>, label: List<IOType>): List<IOType>

    @Serializable
    abstract class D1 : Output {
        protected abstract fun expect(input: List<IOType.D1>): List<IOType.D1>
        protected abstract fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D1>)
        final override fun _train(input: List<IOType>, label: List<IOType>): List<IOType> = train(
            input = input as List<IOType.D1>,
            label = label as List<IOType.D1>,
        )
    }
}

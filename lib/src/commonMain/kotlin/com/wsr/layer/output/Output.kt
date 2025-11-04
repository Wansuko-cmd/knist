package com.wsr.layer.output

import com.wsr.IOType
import com.wsr.layer.Layer
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
sealed interface Output : Layer {
    @Suppress("FunctionName")
    fun _train(input: List<IOType>, label: List<IOType>): List<IOType>

    override fun _train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType> =
        _train(input, calcDelta(input))

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

    @Serializable
    abstract class D2 : Output {
        protected abstract fun expect(input: List<IOType.D2>): List<IOType.D2>

        protected abstract fun train(input: List<IOType.D2>, label: List<IOType.D2>): List<IOType.D2>

        final override fun _expect(input: List<IOType>): List<IOType> = expect(input = input as List<IOType.D2>)

        final override fun _train(input: List<IOType>, label: List<IOType>): List<IOType> = train(
            input = input as List<IOType.D2>,
            label = label as List<IOType.D2>,
        )
    }
}

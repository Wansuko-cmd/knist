package com.wsr.converter.output

import com.wsr.IOType

@Suppress("UNCHECKED_CAST")
sealed interface OutputConverter {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _convert(output: List<IOType>): List<*>

    abstract class D1<T> : OutputConverter {
        abstract val inputSize: Int
        abstract fun convert(output: List<IOType.D1>): List<T>
        final override fun _convert(output: List<IOType>): List<*> = convert(output as List<IOType.D1>)
    }

    abstract class D2<T> : OutputConverter {
        abstract val inputX: Int
        abstract val inputY: Int
        abstract fun convert(output: List<IOType.D2>): List<T>
        final override fun _convert(output: List<IOType>): List<*> = convert(output as List<IOType.D2>)
    }

    abstract class D3<T> : OutputConverter {
        abstract val inputX: Int
        abstract val inputY: Int
        abstract val inputZ: Int
        abstract fun convert(output: List<IOType.D3>): List<T>
        final override fun _convert(output: List<IOType>): List<*> = convert(output as List<IOType.D3>)
    }
}

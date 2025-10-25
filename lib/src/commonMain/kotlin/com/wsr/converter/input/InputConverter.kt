package com.wsr.converter.input

import com.wsr.IOType

@Suppress("UNCHECKED_CAST")
sealed interface InputConverter {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _convert(input: List<*>): List<IOType>

    abstract class D1<T> : InputConverter {
        abstract val outputSize: Int
        abstract fun convert(input: List<T>): List<IOType.D1>
        final override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }

    abstract class D2<T> : InputConverter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract fun convert(input: List<T>): List<IOType.D2>
        final override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }

    abstract class D3<T> : InputConverter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract val outputZ: Int
        abstract fun convert(input: List<T>): List<IOType.D3>
        final override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }
}

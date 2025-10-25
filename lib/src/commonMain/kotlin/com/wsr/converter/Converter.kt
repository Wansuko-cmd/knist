package com.wsr.converter

import com.wsr.IOType

@Suppress("UNCHECKED_CAST")
sealed interface Converter {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _encode(input: List<*>): List<IOType>

    abstract class D1<T> : Converter {
        abstract val outputSize: Int
        abstract fun encode(input: List<T>): List<IOType.D1>
        final override fun _encode(input: List<*>): List<IOType> = encode(input as List<T>)
    }

    abstract class D2<T> : Converter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract fun encode(input: List<T>): List<IOType.D2>
        final override fun _encode(input: List<*>): List<IOType> = encode(input as List<T>)
    }

    abstract class D3<T> : Converter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract val outputZ: Int
        abstract fun encode(input: List<T>): List<IOType.D3>
        final override fun _encode(input: List<*>): List<IOType> = encode(input as List<T>)
    }
}

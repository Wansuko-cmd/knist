package com.wsr.converter

import com.wsr.Batch
import com.wsr.IOType

@Suppress("UNCHECKED_CAST")
sealed interface Converter {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _encode(input: List<*>): Batch<IOType>

    @Suppress("FunctionName")
    fun _decode(input: Batch<IOType>): List<*>

    abstract class D1<T> : Converter {
        abstract val outputSize: Int
        abstract fun encode(input: List<T>): Batch<IOType.D1>
        abstract fun decode(input: Batch<IOType.D1>): List<T>

        final override fun _encode(input: List<*>): Batch<IOType> = encode(input as List<T>)
        final override fun _decode(input: Batch<IOType>): List<*> = decode(input as Batch<IOType.D1>)
    }

    abstract class D2<T> : Converter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract fun encode(input: List<T>): Batch<IOType.D2>
        abstract fun decode(input: Batch<IOType.D2>): List<T>

        final override fun _encode(input: List<*>): Batch<IOType> = encode(input as List<T>)
        final override fun _decode(input: Batch<IOType>): List<*> = decode(input as Batch<IOType.D2>)
    }

    abstract class D3<T> : Converter {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract val outputZ: Int
        abstract fun encode(input: List<T>): Batch<IOType.D3>
        abstract fun decode(input: Batch<IOType.D3>): List<T>

        final override fun _encode(input: List<*>): Batch<IOType> = encode(input as List<T>)
        final override fun _decode(input: Batch<IOType>): List<*> = decode(input as Batch<IOType.D3>)
    }
}

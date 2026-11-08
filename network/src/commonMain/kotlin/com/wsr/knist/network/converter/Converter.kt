package com.wsr.knist.network.converter

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType

@Suppress("UNCHECKED_CAST")
sealed interface Converter<T> {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _encode(input: T): Batch<IOType>

    @Suppress("FunctionName")
    fun _decode(input: Batch<IOType>): T

    abstract class D1<T> : Converter<T> {
        abstract val i: Int
        abstract fun encode(input: T): Batch<IOType.D1>
        abstract fun decode(input: Batch<IOType.D1>): T

        final override fun _encode(input: T): Batch<IOType> = encode(input)
        final override fun _decode(input: Batch<IOType>): T = decode(input as Batch<IOType.D1>)
    }

    abstract class D2<T> : Converter<T> {
        abstract val i: Int
        abstract val j: Int
        abstract fun encode(input: T): Batch<IOType.D2>
        abstract fun decode(input: Batch<IOType.D2>): T

        final override fun _encode(input: T): Batch<IOType> = encode(input)
        final override fun _decode(input: Batch<IOType>): T = decode(input as Batch<IOType.D2>)
    }

    abstract class D3<T> : Converter<T> {
        abstract val i: Int
        abstract val j: Int
        abstract val k: Int
        abstract fun encode(input: T): Batch<IOType.D3>
        abstract fun decode(input: Batch<IOType.D3>): T

        final override fun _encode(input: T): Batch<IOType> = encode(input)
        final override fun _decode(input: Batch<IOType>): T = decode(input as Batch<IOType.D3>)
    }
}

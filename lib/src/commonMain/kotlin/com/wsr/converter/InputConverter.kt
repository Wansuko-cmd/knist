package com.wsr.converter

import com.wsr.IOType

@Suppress("UNCHECKED_CAST")
sealed interface InputConverter {
    // Serializer対策で型情報を削除(Network側で型を担保)
    @Suppress("FunctionName")
    fun _convert(input: List<*>): List<IOType>

    interface D1<T> : InputConverter {
        val outputSize: Int
        fun convert(input: List<T>): List<IOType.D1>
        override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }

    interface D2<T> : InputConverter {
        val outputX: Int
        val outputY: Int
        fun convert(input: List<T>): List<IOType.D2>
        override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }

    interface D3<T> : InputConverter {
        val outputX: Int
        val outputY: Int
        val outputZ: Int
        fun convert(input: List<T>): List<IOType.D3>
        override fun _convert(input: List<*>): List<IOType> = convert(input as List<T>)
    }
}

package com.wsr.converter

import com.wsr.IOType

sealed interface InputConverter<T> {
    fun convert(input: List<T>): List<IOType>

    interface D1<T> : InputConverter<T> {
        val outputSize: Int
        override fun convert(input: List<T>): List<IOType.D1>
    }

    interface D2<T> : InputConverter<T> {
        val outputX: Int
        val outputY: Int
        override fun convert(input: List<T>): List<IOType.D2>
    }

    interface D3<T> : InputConverter<T> {
        val outputX: Int
        val outputY: Int
        val outputZ: Int
        override fun convert(input: List<T>): List<IOType.D3>
    }
}

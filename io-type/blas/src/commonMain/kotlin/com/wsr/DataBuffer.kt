package com.wsr

interface DataBuffer {
    fun toFloatArray(): FloatArray

    fun copyOf(): DataBuffer

    fun get(i: Int): Float
    fun set(i: Int, value: Float)

    class Default(private val value: FloatArray) : DataBuffer {
        override fun toFloatArray(): FloatArray = value

        override fun copyOf(): DataBuffer = Default(value.copyOf())

        override fun get(i: Int): Float = value[i]
        override fun set(i: Int, value: Float) {
            this.value[i] = value
        }
    }

    companion object {
        fun create(value: FloatArray) = Default(value)
    }
}

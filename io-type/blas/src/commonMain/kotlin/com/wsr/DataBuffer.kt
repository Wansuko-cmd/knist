package com.wsr

interface DataBuffer {
    val size: Int

    val indices: IntRange

    fun toFloatArray(): FloatArray

    fun copyOf(): DataBuffer

    operator fun get(i: Int): Float
    operator fun set(i: Int, value: Float)

    fun slice(indices: IntRange): DataBuffer

    fun copyInto(destination: DataBuffer, destinationOffset: Int = 0)

    class Default(private val value: FloatArray) : DataBuffer {
        override val size = value.size
        override val indices: IntRange = value.indices

        override fun toFloatArray(): FloatArray = value

        override fun copyOf(): DataBuffer = Default(value.copyOf())

        override operator fun get(i: Int): Float = value[i]
        override operator fun set(i: Int, value: Float) {
            this.value[i] = value
        }

        override fun slice(indices: IntRange): DataBuffer = DataBuffer.create(value.sliceArray(indices))

        override fun copyInto(destination: DataBuffer, destinationOffset: Int) {
            value.copyInto(destination.toFloatArray(), destinationOffset)
        }
    }

    companion object {
        fun create(value: FloatArray) = Default(value)

        fun create(size: Int) = Default(FloatArray(size))
    }
}

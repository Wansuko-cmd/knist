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

    data class Default(private val value: FloatArray) : DataBuffer {
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Default

            if (size != other.size) return false
            if (!value.contentEquals(other.value)) return false
            if (indices != other.indices) return false

            return true
        }

        override fun hashCode(): Int {
            var result = size
            result = 31 * result + value.contentHashCode()
            result = 31 * result + indices.hashCode()
            return result
        }
    }

    companion object {
        fun create(value: FloatArray) = Default(value)

        fun create(size: Int) = Default(FloatArray(size))
    }
}

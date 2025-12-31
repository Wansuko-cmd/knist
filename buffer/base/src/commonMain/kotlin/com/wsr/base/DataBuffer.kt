package com.wsr.base

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DataBufferSerializable::class)
interface DataBuffer {
    val size: Int

    val indices: IntRange get() = 0 until size

    fun toFloatArray(): FloatArray

    operator fun get(i: Int): Float
    operator fun set(i: Int, value: Float)

    fun slice(indices: IntRange): DataBuffer

    fun copyInto(destination: DataBuffer, destinationOffset: Int = 0)

    companion object
}

@Serializable
data class Default(private val value: FloatArray) : DataBuffer {
    constructor(size: Int) : this(value = FloatArray(size))

    override val size = value.size

    override fun toFloatArray(): FloatArray = value

    override operator fun get(i: Int): Float = value[i]
    override operator fun set(i: Int, value: Float) {
        this.value[i] = value
    }

    override fun slice(indices: IntRange): DataBuffer = Default(value.sliceArray(indices))

    override fun copyInto(destination: DataBuffer, destinationOffset: Int) {
        when (destination) {
            is Default -> value.copyInto(destination.value, destinationOffset)
            else -> for (i in indices) destination[destinationOffset + i] = this[i]
        }
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

object DataBufferSerializable : KSerializer<DataBuffer> {
    override val descriptor: SerialDescriptor = Default.serializer().descriptor
    override fun serialize(encoder: Encoder, value: DataBuffer) {
        encoder.encodeSerializableValue(
            serializer = Default.serializer(),
            value = Default(value.toFloatArray()),
        )
    }

    override fun deserialize(decoder: Decoder): DataBuffer = decoder.decodeSerializableValue(Default.serializer())
}

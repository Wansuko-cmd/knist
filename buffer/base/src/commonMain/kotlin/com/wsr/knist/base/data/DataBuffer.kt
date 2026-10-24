package com.wsr.knist.base.data

import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

@Serializable(with = DataBufferSerializable::class)
interface DataBuffer {
    val size: Int
    operator fun get(i: Int): Float
    operator fun set(i: Int, value: Float)

    fun toFloatArray(): FloatArray
    fun release()

    companion object {
        fun create(size: Int) = DataBufferGenerator.create(size)

        fun create(value: FloatArray) = DataBufferGenerator.create(value)

        @JvmName("createWithElements")
        fun create(vararg elements: Float) = DataBufferGenerator.create(elements)
    }
}

val DataBuffer.indices: IntRange get() = 0 until size

fun DataBuffer.contentEquals(other: DataBuffer) = when {
    this.size != other.size -> false
    else -> this.toFloatArray().contentEquals(other.toFloatArray())
}

val IntProgression.size get() = if (isEmpty()) 0 else (last - first) / step + 1

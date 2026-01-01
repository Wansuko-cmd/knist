package com.wsr.cpu

import com.wsr.base.DataBuffer
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal fun DataBuffer.toCPUBuffer(): CPUBuffer = when (this) {
    is CPUBuffer -> this
    else -> CPUBuffer.create(this.toFloatArray())
}

data class CPUBuffer(internal val byteBuffer: ByteBuffer) : DataBuffer {
    private val floatBuffer = byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer()
    override val size = floatBuffer.capacity()
    override fun toFloatArray(): FloatArray {
        val result = FloatArray(size)
        floatBuffer.move(position = 0, limit = size) { get(result) }
        return result
    }

    override fun get(i: Int): Float = floatBuffer.get(i)

    override fun set(i: Int, value: Float) {
        floatBuffer.put(i, value)
    }

    override fun slice(indices: IntRange): DataBuffer {
        val start = indices.first * Float.SIZE_BYTES
        val length = (indices.last - indices.first + 1) * Float.SIZE_BYTES
        return byteBuffer.move(position = start, limit = start + length) {
            CPUBuffer(slice().order(ByteOrder.nativeOrder()))
        }
    }

    override fun copyInto(destination: DataBuffer, destinationOffset: Int) {
        when (destination) {
            is CPUBuffer -> {
                byteBuffer.move(position = 0, limit = size) {
                    val destBuffer = destination.floatBuffer
                    destBuffer.move(position = destinationOffset, limit = destinationOffset + size) {
                        destBuffer.put(floatBuffer)
                    }
                }
            }

            else -> for (i in indices) destination[i + destinationOffset] = this[i]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is CPUBuffer -> size == other.size && byteBuffer == other.byteBuffer
            is DataBuffer -> size == other.size && this.toFloatArray().contentEquals(other.toFloatArray())
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + this.toFloatArray().contentHashCode()
        return result
    }

    companion object {
        fun create(size: Int): CPUBuffer {
            val buffer = ByteBuffer.allocateDirect(size * Float.SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
            return CPUBuffer(buffer)
        }

        fun create(value: FloatArray): CPUBuffer {
            val buffer = ByteBuffer
                .allocateDirect(value.size * Float.SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .apply { asFloatBuffer().put(value) }
            return CPUBuffer(buffer)
        }
    }
}

private inline fun <B : Buffer, T> B.move(position: Int, limit: Int = limit(), block: B.() -> T): T {
    val currentPosition = position()
    val currentLimit = this.limit()
    position(position)
    limit(limit)
    try {
        return block()
    } finally {
        position(currentPosition)
        limit(currentLimit)
    }
}

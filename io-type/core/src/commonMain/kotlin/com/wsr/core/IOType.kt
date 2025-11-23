package com.wsr.core

import com.wsr.BLAS
import kotlinx.serialization.Serializable

@Serializable
sealed class IOType {
    abstract val value: FloatArray
    abstract val shape: List<Int>
    abstract val size: Int

    @Serializable
    data class D0(private val _value: FloatArray) : IOType() {
        override val value get() = _value
        override val shape = listOf(1)
        override val size = 1

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D1(
        private val _value: FloatArray,
        override val size: Int = _value.size,
    ) : IOType() {
        override val value: FloatArray = _value
        override val shape = listOf(size)

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D2(
        private val _value: FloatArray,
        override val shape: List<Int>,
    ) : IOType() {
        override val value get() = _value
        override val size = shape.reduce { acc, i -> acc + i }
        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D3(
        private val _value: FloatArray,
        override val shape: List<Int>,
    ) : IOType() {
        override val value get() = _value
        override val size = shape.reduce { acc, i -> acc + i }
        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D4(
        private val _value: FloatArray,
        override val shape: List<Int>,
    ) : IOType() {
        override val value get() = _value
        override val size = shape.reduce { acc, i -> acc + i }
        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IOType

        if (!value.contentEquals(other.value)) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.contentHashCode()
        result = 31 * result + shape.hashCode()
        return result
    }

    companion object {
        val enableBLAS get() = BLAS.isNative
    }
}

fun IOType.Companion.d0(value: Float) = IOType.D0(floatArrayOf(value))

inline fun IOType.Companion.d1(size: Int, init: (Int) -> Float = { 0f }): IOType.D1 {
    val value = FloatArray(size)
    for (i in 0 until size) value[i] = init(i)
    return IOType.D1(_value = value)
}

inline fun IOType.Companion.d1(shape: List<Int>, init: (Int) -> Float = { 0f }) = d1(shape[0], init)

fun IOType.Companion.d1(value: List<Float>) = IOType.D1(_value = value.toFloatArray())

fun IOType.Companion.d1(value: FloatArray) = IOType.D1(_value = value)

inline fun IOType.Companion.d2(i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): IOType.D2 {
    val value = FloatArray(i * j)
    for (_i in 0 until i) {
        for (_j in 0 until j) {
            value[_i * j + _j] = init(_i, _j)
        }
    }
    return IOType.D2(shape = listOf(i, j), _value = value)
}

inline fun IOType.Companion.d2(shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }) = d2(
    i = shape[0],
    j = shape[1],
    init = init,
)

fun IOType.Companion.d2(shape: List<Int>, value: List<Float>) = IOType.D2(
    _value = value.toFloatArray(),
    shape = shape,
)

fun IOType.Companion.d2(shape: List<Int>, value: FloatArray) = IOType.D2(shape = shape, _value = value)

inline fun IOType.Companion.d3(i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): IOType.D3 {
    val value = FloatArray(i * j * k)
    for (_i in 0 until i) {
        for (_j in 0 until j) {
            for (_k in 0 until k) {
                value[(_i * j + _j) * k + _k] = init(_i, _j, _k)
            }
        }
    }
    return IOType.D3(shape = listOf(i, j, k), _value = value)
}

inline fun IOType.Companion.d3(shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }) = d3(
    i = shape[0],
    j = shape[1],
    k = shape[2],
    init = init,
)

fun IOType.Companion.d3(shape: List<Int>, value: List<Float>) = IOType.D3(
    _value = value.toFloatArray(),
    shape = shape,
)

fun IOType.Companion.d3(shape: List<Int>, value: FloatArray) = IOType.D3(shape = shape, _value = value)

inline fun IOType.Companion.d4(
    i: Int,
    j: Int,
    k: Int,
    l: Int,
    init: (Int, Int, Int, Int) -> Float = { _, _, _, _ ->
        0f
    },
): IOType.D4 {
    val value = FloatArray(i * j * k * l)
    for (_i in 0 until i) {
        for (_j in 0 until j) {
            for (_k in 0 until k) {
                for (_l in 0 until l) {
                    value[((_i * j + _j) * k + _k) * l + _l] = init(_i, _j, _k, _l)
                }
            }
        }
    }
    return IOType.D4(shape = listOf(i, j, k, l), _value = value)
}

inline fun IOType.Companion.d4(shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }) = d4(
    i = shape[0],
    j = shape[1],
    k = shape[2],
    l = shape[3],
    init = init,
)

fun IOType.Companion.d4(shape: List<Int>, value: List<Float>) = IOType.D4(
    _value = value.toFloatArray(),
    shape = shape,
)

fun IOType.Companion.d4(shape: List<Int>, value: FloatArray) = IOType.D4(shape = shape, _value = value)

/**
 * get
 */
fun IOType.D0.get() = value[0]

operator fun IOType.D1.get(index: Int) = value[index]

operator fun IOType.D2.get(i: Int, j: Int) = value[i * shape[1] + j]

operator fun IOType.D2.get(i: Int): IOType.D1 {
    val offset = i * shape[1]
    return IOType.d1(value.sliceArray(offset until offset + shape[1]))
}

operator fun IOType.D3.get(i: Int, j: Int, k: Int) = value[(i * shape[1] + j) * shape[2] + k]

operator fun IOType.D3.get(i: Int, j: Int): IOType.D1 {
    val offset = (i * shape[1] + j) * shape[2]
    return IOType.d1(value = value.sliceArray(offset until offset + shape[2]))
}

operator fun IOType.D3.get(i: Int): IOType.D2 {
    val offset = i * shape[1] * shape[2]
    return IOType.d2(
        shape = listOf(shape[1], shape[2]),
        value = value.sliceArray(offset until offset + shape[1] * shape[2]),
    )
}

operator fun IOType.D4.get(i: Int, j: Int, k: Int, l: Int) = value[((i * shape[1] + j) * shape[2] + k) * shape[3] + l]

operator fun IOType.D4.get(i: Int, j: Int, k: Int): IOType.D1 {
    val offset = ((i * shape[1] + j) * shape[2] + k) * shape[3]
    return IOType.d1(value = value.sliceArray(offset until offset + shape[3]))
}

operator fun IOType.D4.get(i: Int, j: Int): IOType.D2 {
    val offset = (i * shape[1] + j) * shape[2] * shape[3]
    return IOType.d2(
        shape = listOf(shape[2], shape[3]),
        value = value.sliceArray(offset until offset + shape[2] * shape[3]),
    )
}

operator fun IOType.D4.get(i: Int): IOType.D3 {
    val offset = i * shape[1] * shape[2] * shape[3]
    return IOType.d3(
        shape = listOf(shape[1], shape[2], shape[3]),
        value = value.sliceArray(offset until offset + shape[1] * shape[2] * shape[3]),
    )
}

/**
 * set
 */
fun IOType.D0.set(element: Float) {
    value[0] = element
}

operator fun IOType.D1.set(index: Int, element: Float) {
    value[index] = element
}

operator fun IOType.D2.set(i: Int, j: Int, element: Float) {
    value[i * shape[1] + j] = element
}

operator fun IOType.D2.set(i: Int, element: IOType.D1) {
    element.value.copyInto(
        destination = value,
        destinationOffset = i * shape[1],
    )
}

operator fun IOType.D3.set(i: Int, j: Int, z: Int, element: Float) {
    value[(i * shape[1] + j) * shape[2] + z] = element
}

operator fun IOType.D3.set(i: Int, j: Int, element: IOType.D1) {
    element.value.copyInto(
        destination = value,
        destinationOffset = (i * shape[1] + j) * shape[2],
    )
}

operator fun IOType.D3.set(i: Int, element: IOType.D3) {
    element.value.copyInto(
        destination = value,
        destinationOffset = i * shape[1] * shape[2],
    )
}

operator fun IOType.D4.set(i: Int, j: Int, k: Int, l: Int, element: Float) {
    value[((i * shape[1] + j) * shape[2] + k) * shape[3] + l] = element
}

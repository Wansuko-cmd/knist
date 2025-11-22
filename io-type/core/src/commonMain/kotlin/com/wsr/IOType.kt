package com.wsr

import kotlinx.serialization.Serializable

@Serializable
sealed class IOType {
    abstract val value: FloatArray
    abstract val shape: List<Int>

    @Serializable
    data class D0(override val value: FloatArray) : IOType() {
        override val shape = listOf(1)

        fun get() = value[0]

        fun set(element: Float) {
            value[0] = element
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as D0

            if (!value.contentEquals(other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + value.contentHashCode()
            return result
        }

    }

    @Serializable
    data class D1(override val value: FloatArray) : IOType() {
        override val shape = listOf(value.size)

        operator fun get(index: Int) = value[index]

        operator fun set(index: Int, element: Float) {
            value[index] = element
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D2(override val value: FloatArray, override val shape: List<Int>) : IOType() {
        operator fun get(i: Int, j: Int) = value[i * shape[1] + j]

        operator fun get(i: Int) = d1(value.sliceArray(i * shape[1] until i * shape[1] + shape[1]))

        operator fun set(i: Int, j: Int, element: Float) {
            value[i * shape[1] + j] = element
        }

        operator fun set(i: Int, element: D1) {
            element.value.copyInto(value, i * shape[1])
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D3(override val value: FloatArray, override val shape: List<Int>) : IOType() {
        operator fun get(i: Int, j: Int, k: Int) = value[(i * shape[1] + j) * shape[2] + k]

        operator fun get(i: Int, j: Int): D1 {
            val start = (i * shape[1] + j) * shape[2]
            return d1(value = value.sliceArray(start until start + shape[2]))
        }

        operator fun get(i: Int): D2 {
            val start = i * shape[1] * shape[2]
            return d2(
                shape = listOf(shape[1], shape[2]),
                value = value.sliceArray(start until start + shape[1] * shape[2]),
            )
        }

        operator fun set(i: Int, j: Int, z: Int, element: Float) {
            value[(i * shape[1] + j) * shape[2] + z] = element
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D4(override val value: FloatArray, override val shape: List<Int>) : IOType() {
        operator fun get(i: Int, j: Int, k: Int, l: Int) = value[((i * shape[1] + j) * shape[2] + k) * shape[3] + l]

        operator fun get(i: Int, j: Int, k: Int): D1 {
            val start = ((i * shape[1] + j) * shape[2] + k) * shape[3]
            return d1(value = value.sliceArray(start until start + shape[3]))
        }

        operator fun get(i: Int, j: Int): D2 {
            val start = (i * shape[1] + j) * shape[2] * shape[3]
            return d2(
                shape = listOf(shape[2], shape[3]),
                value = value.sliceArray(start until start + shape[2] * shape[3]),
            )
        }

        operator fun get(i: Int): D3 {
            val start = i * shape[1] * shape[2] * shape[3]
            return d3(
                shape = listOf(shape[1], shape[2], shape[3]),
                value = value.sliceArray(start until start + shape[1] * shape[2] * shape[3]),
            )
        }

        operator fun set(i: Int, j: Int, k: Int, l: Int, element: Float) {
            value[((i * shape[1] + j) * shape[2] + k) * shape[3] + l] = element
        }

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

        fun d0(value: Float) = D0(floatArrayOf(value))

        inline fun d1(size: Int, init: (Int) -> Float = { 0f }): D1 {
            val value = FloatArray(size)
            for (i in 0 until size) value[i] = init(i)
            return D1(value = value)
        }

        inline fun d1(shape: List<Int>, init: (Int) -> Float = { 0f }) = d1(shape[0], init)

        fun d1(value: List<Float>) = D1(value = value.toFloatArray())

        fun d1(value: FloatArray) = D1(value = value)

        inline fun d2(i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): D2 {
            val value = FloatArray(i * j)
            for (_i in 0 until i) {
                for (_j in 0 until j) {
                    value[_i * j + _j] = init(_i, _j)
                }
            }
            return D2(shape = listOf(i, j), value = value)
        }

        inline fun d2(shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }) = d2(
            i = shape[0],
            j = shape[1],
            init = init,
        )

        fun d2(shape: List<Int>, value: List<Float>) = D2(
            value = value.toFloatArray(),
            shape = shape,
        )

        fun d2(shape: List<Int>, value: FloatArray) = D2(shape = shape, value = value)

        inline fun d3(i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): D3 {
            val value = FloatArray(i * j * k)
            for (_i in 0 until i) {
                for (_j in 0 until j) {
                    for (_k in 0 until k) {
                        value[(_i * j + _j) * k + _k] = init(_i, _j, _k)
                    }
                }
            }
            return D3(shape = listOf(i, j, k), value = value)
        }

        inline fun d3(shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }) = d3(
            i = shape[0],
            j = shape[1],
            k = shape[2],
            init = init,
        )

        fun d3(shape: List<Int>, value: List<Float>) = D3(
            value = value.toFloatArray(),
            shape = shape,
        )

        fun d3(shape: List<Int>, value: FloatArray) = D3(shape = shape, value = value)

        inline fun d4(
            i: Int,
            j: Int,
            k: Int,
            l: Int,
            init: (Int, Int, Int, Int) -> Float = { _, _, _, _ ->
                0f
            },
        ): D4 {
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
            return D4(shape = listOf(i, j, k, l), value = value)
        }

        inline fun d4(shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }) = d4(
            i = shape[0],
            j = shape[1],
            k = shape[2],
            l = shape[3],
            init = init,
        )

        fun d4(shape: List<Int>, value: List<Float>) = D4(
            value = value.toFloatArray(),
            shape = shape,
        )

        fun d4(shape: List<Int>, value: FloatArray) = D4(shape = shape, value = value)
    }
}

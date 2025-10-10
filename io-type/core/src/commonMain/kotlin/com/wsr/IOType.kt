@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.wsr

import kotlinx.serialization.Serializable

@Serializable
sealed interface IOType {
    val value: DoubleArray
    val shape: List<Int>
    val enableBLAS get() = BLAS.isNative

    @Serializable
    data class D1(override val value: DoubleArray) : IOType {
        override val shape = listOf(value.size)

        operator fun get(index: Int) = value[index]

        operator fun set(index: Int, element: Double) {
            value[index] = element
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as D1

            if (!value.contentEquals(other.value)) return false
            if (shape != other.shape) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value.contentHashCode()
            result = 31 * result + shape.hashCode()
            return result
        }
    }

    @Serializable
    data class D2(override val value: DoubleArray, override val shape: List<Int>) : IOType {
        operator fun get(x: Int, y: Int) = value[x * shape[1] + y]

        operator fun get(x: Int) = d1(value.sliceArray(x * shape[1] until x * shape[1] + shape[1]))

        operator fun set(x: Int, y: Int, element: Double) {
            value[x * shape[1] + y] = element
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as D2

            if (!value.contentEquals(other.value)) return false
            if (shape != other.shape) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value.contentHashCode()
            result = 31 * result + shape.hashCode()
            return result
        }
    }

    @Serializable
    data class D3(override val value: DoubleArray, override val shape: List<Int>) : IOType {
        operator fun get(x: Int, y: Int, z: Int) = value[(x * shape[1] + y) * shape[2] + z]

        operator fun get(x: Int, y: Int) = d1(shape[2]) { z -> value[(x * shape[1] + y) * shape[2] + z] }

        operator fun get(x: Int) = d2(shape[1], shape[2]) { y, z ->
            value[
                (x * shape[1] + y) *
                    shape[2] +
                    z,
            ]
        }

        operator fun set(x: Int, y: Int, z: Int, element: Double) {
            value[(x * shape[1] + y) * shape[2] + z] = element
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as D3

            if (!value.contentEquals(other.value)) return false
            if (shape != other.shape) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value.contentHashCode()
            result = 31 * result + shape.hashCode()
            return result
        }
    }

    companion object {
        inline fun d1(size: Int, init: (Int) -> Double = { 0.0 }): D1 {
            val value = DoubleArray(size)
            for (i in 0 until size) value[i] = init(i)
            return D1(value = value)
        }

        inline fun d1(shape: List<Int>, init: (Int) -> Double = { 0.0 }) = d1(shape[0], init)

        fun d1(value: List<Double>) = D1(value = value.toDoubleArray())

        fun d1(value: DoubleArray) = D1(value = value)

        inline fun d2(x: Int, y: Int, init: (Int, Int) -> Double): D2 {
            val value = DoubleArray(x * y)
            for (i in 0 until x) {
                for (j in 0 until y) {
                    value[i * y + j] = init(i, j)
                }
            }
            return D2(shape = listOf(x, y), value = value)
        }

        inline fun d2(shape: List<Int>, init: (Int, Int) -> Double = { _, _ -> 0.0 }) = d2(
            x = shape[0],
            y = shape[1],
            init = init,
        )

        fun d2(shape: List<Int>, value: List<Double>) = D2(
            value = value.toDoubleArray(),
            shape = shape,
        )

        fun d2(shape: List<Int>, value: DoubleArray) = D2(shape = shape, value = value)

        inline fun d3(x: Int, y: Int, z: Int, init: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): D3 {
            val value = DoubleArray(x * y * z)
            for (i in 0 until x) {
                for (j in 0 until y) {
                    for (k in 0 until z) {
                        value[(i * y + j) * z + k] = init(i, j, k)
                    }
                }
            }
            return D3(shape = listOf(x, y, z), value = value)
        }

        inline fun d3(shape: List<Int>, init: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }) = d3(
            x = shape[0],
            y = shape[1],
            z = shape[2],
            init = init,
        )

        fun d3(shape: List<Int>, value: List<Double>) = D3(
            value = value.toDoubleArray(),
            shape = shape,
        )

        fun d3(shape: List<Int>, value: DoubleArray) = D3(shape = shape, value = value)
    }
}

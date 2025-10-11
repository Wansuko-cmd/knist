package com.wsr

import kotlinx.serialization.Serializable

@Serializable
sealed class IOType {
    abstract val value: DoubleArray
    abstract val shape: List<Int>

    @Serializable
    data class D1(override val value: DoubleArray) : IOType() {
        override val shape = listOf(value.size)

        operator fun get(index: Int) = value[index]

        operator fun set(index: Int, element: Double) {
            value[index] = element
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D2(override val value: DoubleArray, override val shape: List<Int>) : IOType() {
        operator fun get(x: Int, y: Int) = value[x * shape[1] + y]

        operator fun get(x: Int) = d1(value.sliceArray(x * shape[1] until x * shape[1] + shape[1]))

        operator fun set(x: Int, y: Int, element: Double) {
            value[x * shape[1] + y] = element
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D3(override val value: DoubleArray, override val shape: List<Int>) : IOType() {
        operator fun get(x: Int, y: Int, z: Int) = value[(x * shape[1] + y) * shape[2] + z]

        operator fun get(x: Int, y: Int): D1 {
            val start = (x * shape[1] + y) * shape[2]
            return d1(value = value.sliceArray(start until start + shape[2]))
        }

        operator fun get(x: Int): D2 {
            val start = x * shape[1] * shape[2]
            return d2(
                shape = listOf(shape[1], shape[2]),
                value = value.sliceArray(start until start + shape[1] * shape[2]),
            )
        }

        operator fun set(x: Int, y: Int, z: Int, element: Double) {
            value[(x * shape[1] + y) * shape[2] + z] = element
        }

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun hashCode(): Int = super.hashCode()
    }

    @Serializable
    data class D4(override val value: DoubleArray, override val shape: List<Int>) : IOType() {
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

        operator fun set(i: Int, j: Int, k: Int, l: Int, element: Double) {
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

        inline fun d4(
            i: Int,
            j: Int,
            k: Int,
            l: Int,
            init: (Int, Int, Int, Int) -> Double = { _, _, _, _ ->
                0.0
            },
        ): D4 {
            val value = DoubleArray(i * j * k * l)
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

        inline fun d4(shape: List<Int>, init: (Int, Int, Int, Int) -> Double = { _, _, _, _ -> 0.0 }) = d4(
            i = shape[0],
            j = shape[1],
            k = shape[2],
            l = shape[3],
            init = init,
        )

        fun d4(shape: List<Int>, value: List<Double>) = D4(
            value = value.toDoubleArray(),
            shape = shape,
        )

        fun d4(shape: List<Int>, value: DoubleArray) = D4(shape = shape, value = value)
    }
}

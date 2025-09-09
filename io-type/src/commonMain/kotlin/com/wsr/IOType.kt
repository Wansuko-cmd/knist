@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.wsr

import kotlinx.serialization.Serializable

@Serializable
sealed interface IOType {
    val value: MutableList<Double>
    val shape: List<Int>

    @ConsistentCopyVisibility
    @Serializable
    data class D1 internal constructor(override val value: MutableList<Double>) : IOType {
        override val shape = listOf(value.size)
        operator fun get(index: Int) = value[index]
        operator fun set(index: Int, element: Double) {
            value[index] = element
        }
    }

    @ConsistentCopyVisibility
    @Serializable
    data class D2 internal constructor(
        override val value: MutableList<Double>,
        override val shape: List<Int>,
    ) : IOType {
        operator fun get(x: Int, y: Int) = value[x * shape[1] + y]
        operator fun set(x: Int, y: Int, element: Double) {
            value[x * shape[1] + y] = element
        }
    }

    @ConsistentCopyVisibility
    @Serializable
    data class D3 internal constructor(
        override val value: MutableList<Double>,
        override val shape: List<Int>,
    ) : IOType {
        operator fun get(x: Int, y: Int, z: Int) = value[(x * shape[1] + y) * shape[2] + z]
        operator fun set(x: Int, y: Int, z: Int, element: Double) {
            value[(x * shape[1] + y) * shape[2] + z] = element
        }
    }

    companion object {
        fun d1(vararg elements: Double) = D1(value = mutableListOf(*elements.toTypedArray()))
        fun d1(size: Int, init: (Int) -> Double = { 0.0 }) = D1(value = MutableList(size, init))
        fun d1(shape: List<Int>, init: (Int) -> Double = { 0.0 }) = d1(shape[0], init)
        fun d1(value: List<Double>) = D1(value = value.toMutableList())

        fun d2(x: Int, y: Int, init: (Int, Int) -> Double = { _, _ -> 0.0 }) = D2(
            value = (0 until x).flatMap { x1 ->
                (0 until y).map { y1 -> init(x1, y1) }
            }.toMutableList(),
            shape = listOf(x, y),
        )

        fun d2(shape: List<Int>, init: (Int, Int) -> Double = { _, _ -> 0.0 }) = d2(
            x = shape[0],
            y = shape[1],
            init = init,
        )

        fun d2(shape: List<Int>, value: List<Double>) = D2(
            value = value.toMutableList(),
            shape = shape,
        )

        fun d3(x: Int, y: Int, z: Int, init: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }) = D3(
            value = (0 until x).flatMap { x1 ->
                (0 until y).flatMap { y1 ->
                    (0 until z).map { z1 -> init(x1, y1, z1) }
                }
            }.toMutableList(),
            shape = listOf(x, y, z),
        )

        fun d3(shape: List<Int>, init: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }) = d3(
            x = shape[0],
            y = shape[1],
            z = shape[2],
            init = init,
        )

        fun d3(shape: List<Int>, value: List<Double>) = D3(
            value = value.toMutableList(),
            shape = shape,
        )
    }
}

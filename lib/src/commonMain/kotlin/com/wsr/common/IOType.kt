@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.wsr.common

import kotlinx.serialization.Serializable

@Serializable
sealed interface IOType {
    val value: MutableList<Double>
    val shape: List<Int>

    @Serializable
    data class D1(override val value: MutableList<Double>) : IOType {
        override val shape = listOf(value.size)
        operator fun get(index: Int) = value[index]
        operator fun set(index: Int, element: Double) {
            value[index] = element
        }

        constructor(vararg elements: Double) : this(value = mutableListOf(*elements.toTypedArray()))

        constructor(size: Int, init: (Int) -> Double) : this(value = MutableList(size, init))
    }

    @Serializable
    data class D2(
        override val value: MutableList<Double>,
        override val shape: List<Int>,
    ) : IOType {
        operator fun get(x: Int, y: Int) = value[x * shape[1] + y]
        operator fun set(x: Int, y: Int, element: Double) {
            value[x * shape[1] + y] = element
        }

        constructor(x: Int, y: Int, init: (Int, Int) -> Double) : this(
            value = (0 until x).flatMap { x1 ->
                (0 until y).map { y1 -> init(x1, y1) }
            }.toMutableList(),
            shape = listOf(x, y),
        )
    }

    @Serializable
    data class D3(
        override val value: MutableList<Double>,
        override val shape: List<Int>,
    ) : IOType {
        operator fun get(x: Int, y: Int, z: Int) = value[(x * shape[1] + y) * shape[2] + z]
        operator fun set(x: Int, y: Int, z: Int, element: Double) {
            value[(x * shape[1] + y) * shape[2] + z] = element
        }

        constructor(x: Int, y: Int, z: Int, init: (Int, Int, Int) -> Double) : this(
            value = (0 until x).flatMap { x1 ->
                (0 until y).flatMap { y1 ->
                    (0 until z).map { z1 -> init(x1, y1, z1) }
                }
            }.toMutableList(),
            shape = listOf(x, y, z),
        )
    }
}

@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.wsr.common

import kotlinx.serialization.Serializable

@Serializable
sealed interface IOType {
    val value: MutableList<Double>
    val shape: List<Int>

    fun toD1(): D1

    @Serializable
    data class D1(override val value: MutableList<Double>) : IOType {
        override val shape = listOf(value.size)
        operator fun get(index: Int) = value[index]
        operator fun set(index: Int, element: Double) {
            value[index] = element
        }

        override fun toD1(): D1 = this

        constructor(vararg elements: Double) : this(value = mutableListOf(*elements.toTypedArray()))

        constructor(size: Int, init: (Int) -> Double) : this(value = MutableList(size, init))
    }

    @Serializable
    data class D2(
        override val value: MutableList<Double>,
        override val shape: List<Int>,
    ) : IOType {
        operator fun get(x: Int, y: Int) = value[x * y + y]
        operator fun set(x: Int, y: Int, element: Double) {
            value[x * y + y] = element
        }

        override fun toD1(): D1 = D1(value)

        constructor(x: Int, y: Int, init: (Int, Int) -> Double) : this(
            value = (0 until x).flatMap { x1 ->
                (0 until y).map { y1 -> init(x1, y1) }
            }.toMutableList(),
            shape = listOf(x, y),
        )
    }
}

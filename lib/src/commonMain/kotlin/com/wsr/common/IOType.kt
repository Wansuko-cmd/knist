@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.wsr.common

import kotlinx.serialization.Serializable

@Serializable
sealed interface IOType {
    @Serializable
    data class D1(val value: MutableList<Double>): IOType, MutableList<Double> by value {
        constructor(vararg elements: Double): this(value = mutableListOf(*elements.toTypedArray()))
        constructor(size: Int, init: (Int) -> Double): this(MutableList(size, init))
    }
}

typealias IOTypeD1 = Array<Double>
typealias IOTypeD2 = Array<Array<Double>>

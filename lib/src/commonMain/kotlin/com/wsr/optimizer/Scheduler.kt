package com.wsr.optimizer

import kotlinx.serialization.Serializable
import kotlin.math.pow

sealed interface Scheduler {
    fun calcRate(step: Int): Float

    @Serializable
    data class Fix(val rate: Float) : Scheduler {
        override fun calcRate(step: Int): Float = rate
    }

    @Serializable
    data class Step(val rate: Float, val stepSize: Int, val gamma: Float) : Scheduler {
        override fun calcRate(step: Int): Float = rate * gamma.pow(step / stepSize)
    }
}

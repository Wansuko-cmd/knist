package com.wsr.optimizer

import kotlinx.serialization.Serializable

sealed interface Scheduler {
    fun calcRate(step: Int): Float

    @Serializable
    data class Fix(val rate: Float) : Scheduler {
        override fun calcRate(step: Int): Float = rate
    }
}

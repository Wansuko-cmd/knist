package com.wsr.optimizer

import kotlinx.serialization.Serializable

sealed interface Scheduler {
    fun calcRate(): Float

    @Serializable
    data class Fix(val rate: Float) : Scheduler {
        override fun calcRate(): Float = rate
    }
}

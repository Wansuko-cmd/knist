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
    data class Step(
        val rate: Float,
        val gamma: Float,
        val stepSize: Int,
        val stepUnit: Int = 1,
    ) : Scheduler {
        override fun calcRate(step: Int): Float {
            val count = step / stepUnit
            return rate * gamma.pow(count / stepSize)
        }
    }

    @Serializable
    data class MultiStep(
        val rate: Float,
        val gamma: Float,
        val milestones: List<Int>,
        val stepUnit: Int = 1,
    ) : Scheduler {
        override fun calcRate(step: Int): Float {
            val count = step / stepUnit
            return rate * gamma.pow(milestones.count { it < count })
        }
    }
}

package com.wsr.optimizer

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlinx.serialization.Serializable

sealed interface Scheduler {
    fun calcRate(step: Int): Float

    @Serializable
    data class Fix(val rate: Float) : Scheduler {
        override fun calcRate(step: Int): Float = rate
    }

    @Serializable
    data class Step(val rate: Float, val gamma: Float, val stepCount: Int) : Scheduler {
        override fun calcRate(step: Int): Float = rate * gamma.pow(step / stepCount)
    }

    @Serializable
    data class MultiStep(val rate: Float, val gamma: Float, val milestones: List<Int>) : Scheduler {
        override fun calcRate(step: Int): Float = rate * gamma.pow(milestones.count { it < step })
    }

    @Serializable
    data class CosineAnnealing(
        val minRate: Float,
        val maxRate: Float,
        val stepSize: Int,
        val stepUnit: Int = 1,
        val warmUp: Int = 0,
        val initialRate: Float = minRate,
    ) : Scheduler {
        override fun calcRate(step: Int): Float = if (step < warmUp) {
            initialRate + (maxRate - initialRate) * (step / warmUp.toFloat())
        } else {
            val elapsed = step - warmUp
            val angle = (elapsed % stepSize) / stepSize.toFloat()
            minRate + 0.5f * (maxRate - minRate) * (1 + cos(angle * PI.toFloat()))
        }
    }
}

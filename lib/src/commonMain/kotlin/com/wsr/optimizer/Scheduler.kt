package com.wsr.optimizer

import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
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
        val stepCount: Int,
        val stepUnit: Int = 1,
    ) : Scheduler {
        override fun calcRate(step: Int): Float {
            val count = step / stepUnit
            return rate * gamma.pow(count / stepCount)
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

    @Serializable
    data class CosineAnnealing(
        val minRate: Float,
        val maxRate: Float,
        val stepSize: Int,
        val stepUnit: Int = 1,
        val warmUp: Int = 0,
        val initialRate: Float = minRate,
    ) : Scheduler {
        override fun calcRate(step: Int): Float {
            val t = step / stepUnit
            return if (t < warmUp) {
                initialRate + (maxRate - initialRate) * (t / warmUp.toFloat())
            } else {
                val elapsed = t - warmUp
                val angle = (elapsed % stepSize) / stepSize.toFloat()
                minRate + 0.5f * (maxRate - minRate) * (1 + cos(angle * PI.toFloat()))
            }
        }
    }
}

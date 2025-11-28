package com.wsr.optimizer

sealed interface Scheduler {
    fun calcRate(): Float
}
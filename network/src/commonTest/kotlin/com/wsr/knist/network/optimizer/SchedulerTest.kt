@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network.optimizer

import com.wsr.knist.network.networkTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class SchedulerTest {
    @Test
    fun `Fix=指定値`() = networkTestRule {
        val target = Scheduler.Fix(rate = 1f)

        assertEquals(expected = 1f, actual = target.calcRate(0))
        assertEquals(expected = 1f, actual = target.calcRate(100))
    }

    @Test
    fun `Step=指定ステップ数を超えた数だけgammaを掛ける`() = networkTestRule {
        val target = Scheduler.Step(rate = 1f, gamma = 0.5f, stepCount = 100)

        assertEquals(expected = 1f, actual = target.calcRate(0))
        assertEquals(expected = 0.5f, actual = target.calcRate(100))
        assertEquals(expected = 0.25f, actual = target.calcRate(200))
    }

    @Test
    fun `MultiStep=マイルストーンを超えた数だけgammaを掛ける`() = networkTestRule {
        val target = Scheduler.MultiStep(rate = 1f, gamma = 0.5f, milestones = listOf(50, 100))

        assertEquals(expected = 1f, actual = target.calcRate(0))
        assertEquals(expected = 1f, actual = target.calcRate(49))
        assertEquals(expected = 0.5f, actual = target.calcRate(50))
        assertEquals(expected = 0.5f, actual = target.calcRate(51))
        assertEquals(expected = 0.25f, actual = target.calcRate(101))
    }

    @Test
    fun `CosineAnnealing=コサイン減衰`() = networkTestRule {
        val target = Scheduler.CosineAnnealing(minRate = 0.1f, maxRate = 1f, stepSize = 100, warmUp = 10)

        assertEquals(expected = 0.1f, actual = target.calcRate(0))
        assertEquals(expected = 1f, actual = target.calcRate(10))
        assertEquals(expected = 0.6890f, actual = target.calcRate(50), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.1220f, actual = target.calcRate(100), absoluteTolerance = 1e-4f)
    }
}

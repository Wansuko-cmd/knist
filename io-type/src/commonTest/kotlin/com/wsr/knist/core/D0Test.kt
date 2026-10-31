@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import kotlin.test.Test
import kotlin.test.assertEquals

class D0Test {
    @Test
    fun `d0_Float=FloatからD0を作成`() {
        val d0 = IOType.d0(1.5f)
        assertEquals(expected = 1.5f, actual = d0.unwrap())
    }

    @Test
    fun `d0_FloatArray=FloatArrayからD0を作成`() {
        val d0 = IOType.d0(floatArrayOf(2.5f))
        assertEquals(expected = 2.5f, actual = d0.unwrap())
    }

    @Test
    fun `get=D0スカラー値取得`() {
        val d0 = IOType.d0(3.0f)
        assertEquals(expected = 3.0f, actual = d0.unwrap())
    }

    @Test
    fun `shape=D0の形状はlistOf1`() {
        val d0 = IOType.d0(1.0f)
        assertEquals(expected = listOf(1), actual = d0.shape)
    }

    @Test
    fun `size=D0のサイズは1`() {
        val d0 = IOType.d0(1.0f)
        assertEquals(expected = 1, actual = d0.size)
    }
}

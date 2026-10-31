@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import kotlin.test.Test
import kotlin.test.assertEquals

class D1Test {
    @Test
    fun `get=1次元要素取得`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f))
        assertEquals(expected = 1.0f, actual = d1[0].unwrap())
        assertEquals(expected = 2.0f, actual = d1[1].unwrap())
        assertEquals(expected = 3.0f, actual = d1[2].unwrap())
        assertEquals(expected = 4.0f, actual = d1[3].unwrap())
    }

    @Test
    fun `d1_init=ラムダでD1を作成`() {
        val d1 = IOType.d1(3) { i -> (i + 1).toFloat() }
        assertEquals(expected = 1.0f, actual = d1[0].unwrap())
        assertEquals(expected = 2.0f, actual = d1[1].unwrap())
        assertEquals(expected = 3.0f, actual = d1[2].unwrap())
    }

    @Test
    fun `d1_vararg=可変長引数でD1を作成`() {
        val d1 = IOType.d1(1.0f, 2.0f, 3.0f)
        assertEquals(expected = 1.0f, actual = d1[0].unwrap())
        assertEquals(expected = 2.0f, actual = d1[1].unwrap())
        assertEquals(expected = 3.0f, actual = d1[2].unwrap())
    }

    @Test
    fun `shape=D1の形状`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        assertEquals(expected = listOf(3), actual = d1.shape)
    }

    @Test
    fun `size=D1のサイズ`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        assertEquals(expected = 3, actual = d1.size)
    }

    @Test
    fun `unwrap=D1の全要素をFloatArrayで取得`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        assertEquals(expected = listOf(1.0f, 2.0f, 3.0f), actual = d1.unwrap().toList())
    }

    @Test
    fun `unwrap=返されたFloatArrayを書き換えても元の値は変わらない`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        d1.unwrap()[0] = 99.0f
        assertEquals(expected = listOf(1.0f, 2.0f, 3.0f), actual = d1.unwrap().toList())
    }
}

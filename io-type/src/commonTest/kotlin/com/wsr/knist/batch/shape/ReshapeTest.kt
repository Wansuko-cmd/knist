@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch.shape

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.d4
import com.wsr.knist.core.IOType
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ReshapeTest {
    private val values = FloatArray(24) { it.toFloat() }

    private fun assertReshaped(actual: Batch<IOType>, shape: List<Int>) {
        assertEquals(expected = shape, actual = actual.shape)
        assertEquals(expected = 2, actual = actual.size)
        assertEquals(expected = values.toList(), actual = actual.value.toFloatArray().toList())
    }

    @Test
    fun `reshape=D1バッチから各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = Batch.d1(size = 2, i = 12, value = values)
        assertReshaped(src.reshapeToD1(), listOf(12))
        assertReshaped(src.reshapeToD2(3, 4), listOf(3, 4))
        assertReshaped(src.reshapeToD2(listOf(3, 4)), listOf(3, 4))
        assertReshaped(src.reshapeToD3(2, 3, 2), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 2)), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD4(2, 3, 2, 1), listOf(2, 3, 2, 1))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 1)), listOf(2, 3, 2, 1))
    }

    @Test
    fun `reshape=D2バッチから各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = Batch.d2(size = 2, i = 3, j = 4, value = values)
        assertReshaped(src.reshapeToD1(), listOf(12))
        assertReshaped(src.reshapeToD2(3, 4), listOf(3, 4))
        assertReshaped(src.reshapeToD2(listOf(3, 4)), listOf(3, 4))
        assertReshaped(src.reshapeToD3(2, 3, 2), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 2)), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD4(2, 3, 2, 1), listOf(2, 3, 2, 1))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 1)), listOf(2, 3, 2, 1))
    }

    @Test
    fun `reshape=D3バッチから各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = Batch.d3(size = 2, i = 2, j = 3, k = 2, value = values)
        assertReshaped(src.reshapeToD1(), listOf(12))
        assertReshaped(src.reshapeToD2(3, 4), listOf(3, 4))
        assertReshaped(src.reshapeToD2(listOf(3, 4)), listOf(3, 4))
        assertReshaped(src.reshapeToD3(2, 3, 2), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 2)), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD4(2, 3, 2, 1), listOf(2, 3, 2, 1))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 1)), listOf(2, 3, 2, 1))
    }

    @Test
    fun `reshape=D4バッチから各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = Batch.d4(size = 2, i = 2, j = 3, k = 2, l = 1, value = values)
        assertReshaped(src.reshapeToD1(), listOf(12))
        assertReshaped(src.reshapeToD2(3, 4), listOf(3, 4))
        assertReshaped(src.reshapeToD2(listOf(3, 4)), listOf(3, 4))
        assertReshaped(src.reshapeToD3(2, 3, 2), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 2)), listOf(2, 3, 2))
        assertReshaped(src.reshapeToD4(2, 3, 2, 1), listOf(2, 3, 2, 1))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 1)), listOf(2, 3, 2, 1))
    }

    @Test
    fun `reshape=変形後も元とDataBufferを共有する`() = ioTypeTestRule {
        val src = Batch.d2(size = 2, i = 3, j = 4, value = values)
        assertSame(expected = src.value, actual = src.reshapeToD3(2, 3, 2).value)
        assertSame(expected = src.value, actual = src.reshapeToD1().value)
    }
}

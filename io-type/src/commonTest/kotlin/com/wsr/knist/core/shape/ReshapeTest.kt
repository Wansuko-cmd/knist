@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core.shape

import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.d4
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ReshapeTest {
    private val values = FloatArray(24) { it.toFloat() }

    private fun assertReshaped(actual: IOType, shape: List<Int>) {
        assertEquals(expected = shape, actual = actual.shape)
        assertEquals(expected = values.toList(), actual = actual.value.toFloatArray().toList())
    }

    @Test
    fun `reshape=D1から各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = IOType.d1(values)
        assertReshaped(src.reshapeToD1(), listOf(24))
        assertReshaped(src.reshapeToD2(4, 6), listOf(4, 6))
        assertReshaped(src.reshapeToD2(listOf(4, 6)), listOf(4, 6))
        assertReshaped(src.reshapeToD3(2, 3, 4), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 4)), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD4(2, 3, 2, 2), listOf(2, 3, 2, 2))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 2)), listOf(2, 3, 2, 2))
    }

    @Test
    fun `reshape=D2から各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = IOType.d2(listOf(4, 6), values)
        assertReshaped(src.reshapeToD1(), listOf(24))
        assertReshaped(src.reshapeToD2(4, 6), listOf(4, 6))
        assertReshaped(src.reshapeToD2(listOf(4, 6)), listOf(4, 6))
        assertReshaped(src.reshapeToD3(2, 3, 4), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 4)), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD4(2, 3, 2, 2), listOf(2, 3, 2, 2))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 2)), listOf(2, 3, 2, 2))
    }

    @Test
    fun `reshape=D3から各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = IOType.d3(listOf(2, 3, 4), values)
        assertReshaped(src.reshapeToD1(), listOf(24))
        assertReshaped(src.reshapeToD2(4, 6), listOf(4, 6))
        assertReshaped(src.reshapeToD2(listOf(4, 6)), listOf(4, 6))
        assertReshaped(src.reshapeToD3(2, 3, 4), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 4)), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD4(2, 3, 2, 2), listOf(2, 3, 2, 2))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 2)), listOf(2, 3, 2, 2))
    }

    @Test
    fun `reshape=D4から各次元へList指定とijkl指定で変形できる`() = ioTypeTestRule {
        val src = IOType.d4(listOf(2, 3, 2, 2), values)
        assertReshaped(src.reshapeToD1(), listOf(24))
        assertReshaped(src.reshapeToD2(4, 6), listOf(4, 6))
        assertReshaped(src.reshapeToD2(listOf(4, 6)), listOf(4, 6))
        assertReshaped(src.reshapeToD3(2, 3, 4), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD3(listOf(2, 3, 4)), listOf(2, 3, 4))
        assertReshaped(src.reshapeToD4(2, 3, 2, 2), listOf(2, 3, 2, 2))
        assertReshaped(src.reshapeToD4(listOf(2, 3, 2, 2)), listOf(2, 3, 2, 2))
    }

    @Test
    fun `reshape=変形後も元とDataBufferを共有する`() = ioTypeTestRule {
        val src = IOType.d2(listOf(4, 6), values)
        assertSame(expected = src.value, actual = src.reshapeToD3(2, 3, 4).value)
        assertSame(expected = src.value, actual = src.reshapeToD1().value)
    }
}

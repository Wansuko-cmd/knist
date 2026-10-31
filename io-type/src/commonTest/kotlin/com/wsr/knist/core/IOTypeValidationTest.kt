@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.shape.reshapeToD2
import com.wsr.knist.core.shape.reshapeToD3
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertFailsWith

class IOTypeValidationTest {
    @Test
    fun `D0=バッファの要素数が1でない場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { IOType.D0.Global(DataBuffer.create(2)) }
        assertFailsWith<IllegalArgumentException> { IOType.D0.Local(DataBuffer.create(0)) }
    }

    @Test
    fun `D2からD4=shapeの要素数の積とバッファの要素数が一致しない場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { IOType.D2.Global(DataBuffer.create(6), listOf(2, 2)) }
        assertFailsWith<IllegalArgumentException> { IOType.D3.Local(DataBuffer.create(6), listOf(2, 2, 2)) }
        assertFailsWith<IllegalArgumentException> { IOType.D4.Global(DataBuffer.create(6), listOf(1, 2, 2, 2)) }
    }

    @Test
    fun `D2からD4=shapeの次元数が一致しない場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { IOType.D2.Global(DataBuffer.create(6), listOf(6)) }
        assertFailsWith<IllegalArgumentException> { IOType.D3.Global(DataBuffer.create(6), listOf(2, 3)) }
        assertFailsWith<IllegalArgumentException> { IOType.D4.Global(DataBuffer.create(6), listOf(2, 3, 1)) }
    }

    @Test
    fun `D2からD4=shapeに負の値がある場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { IOType.D2.Global(DataBuffer.create(6), listOf(-2, -3)) }
    }

    @Test
    fun `reshape=要素数が一致しない変形は例外`() = ioTypeTestRule {
        val d2 = IOType.d2(listOf(4, 6)) { _, _ -> 0f }
        assertFailsWith<IllegalArgumentException> { d2.reshapeToD2(5, 5) }
        assertFailsWith<IllegalArgumentException> { d2.reshapeToD3(listOf(2, 3, 5)) }
    }

    @Test
    fun `reshape=shapeの次元数が変換先と一致しない場合は例外`() = ioTypeTestRule {
        val d2 = IOType.d2(listOf(4, 6)) { _, _ -> 0f }
        assertFailsWith<IllegalArgumentException> { d2.reshapeToD2(listOf(24)) }
        assertFailsWith<IllegalArgumentException> { d2.reshapeToD3(listOf(4, 6)) }
    }
}

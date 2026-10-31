@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.shape.reshapeToD2
import com.wsr.knist.batch.shape.reshapeToD3
import com.wsr.knist.core.IOType
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BatchValidationTest {
    @Test
    fun `バッファの要素数がsizeとshapeの積と一致しない場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { Batch<IOType.D1>(DataBuffer.create(5), 2, listOf(3)) }
        assertFailsWith<IllegalArgumentException> { Batch<IOType.D2>(DataBuffer.create(12), 2, listOf(2, 2)) }
    }

    @Test
    fun `sizeが負の場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { Batch<IOType.D1>(DataBuffer.create(0), -1, listOf(3)) }
    }

    @Test
    fun `shapeに負の値がある場合は例外`() = ioTypeTestRule {
        assertFailsWith<IllegalArgumentException> { Batch<IOType.D2>(DataBuffer.create(6), 1, listOf(-2, -3)) }
    }

    @Test
    fun `一致している場合は作成できる`() = ioTypeTestRule {
        val batch = Batch<IOType.D2>(DataBuffer.create(12), 2, listOf(2, 3))
        assertEquals(expected = 2, actual = batch.size)
    }

    @Test
    fun `reshape=1要素あたりの要素数が一致しない変形は例外`() = ioTypeTestRule {
        val batch = Batch.d2(size = 2, i = 3, j = 4, value = FloatArray(24))
        assertFailsWith<IllegalArgumentException> { batch.reshapeToD2(5, 5) }
        assertFailsWith<IllegalArgumentException> { batch.reshapeToD3(listOf(2, 3, 3)) }
    }
}

@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.reshape.transpose.transpose
import kotlin.test.Test
import kotlin.test.assertEquals

class D3ExtTest {
    @Test
    fun `D3のtranspose_0_1_2=元のD3と同じ`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 0, axisJ = 1, axisK = 2)
        assertEquals(
            expected = a,
            actual = result,
        )
    }

    @Test
    fun `D3のtranspose_0_2_1=軸1と軸2を入れ替え`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 0, axisJ = 2, axisK = 1)
        assertEquals(
            expected = IOType.d3(2, 4, 3) { x, y, z -> (x * 12 + z * 4 + y + 1).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `D3のtranspose_1_0_2=軸0と軸1を入れ替え`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 1, axisJ = 0, axisK = 2)
        assertEquals(
            expected = IOType.d3(3, 2, 4) { x, y, z -> (y * 12 + x * 4 + z + 1).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `D3のtranspose_1_2_0=軸を巡回シフト`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 1, axisJ = 2, axisK = 0)
        assertEquals(
            expected = IOType.d3(3, 4, 2) { x, y, z -> (z * 12 + x * 4 + y + 1).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `D3のtranspose_2_0_1=軸を逆巡回シフト`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 2, axisJ = 0, axisK = 1)
        assertEquals(
            expected = IOType.d3(4, 2, 3) { x, y, z -> (y * 12 + z * 4 + x + 1).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `D3のtranspose_2_1_0=軸0と軸2を入れ替え`() {
        val a = IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 1).toFloat() }
        val result = a.transpose(axisI = 2, axisJ = 1, axisK = 0)
        assertEquals(
            expected = IOType.d3(4, 3, 2) { x, y, z -> (z * 12 + y * 4 + x + 1).toFloat() },
            actual = result,
        )
    }
}

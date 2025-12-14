@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.reshape.transpose.transpose
import kotlin.test.Test
import kotlin.test.assertEquals

class D2ExtTest {
    @Test
    fun `D2のtranspose=行と列を入れ替えたD2`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        // [[1, 3, 5],
        //  [2, 4, 6]]
        val result = a.transpose()
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (y * 2 + x + 1).toFloat() },
            actual = result,
        )
    }
}

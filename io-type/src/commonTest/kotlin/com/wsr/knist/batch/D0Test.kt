@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d0
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test

class D0Test {
    @Test
    fun `get=D0バッチ要素取得`() = ioTypeTestRule {
        val batch = Batch.of(IOType.d0(1f), IOType.d0(2f), IOType.d0(3f))
        assertContentEquals(IOType.d0(1f), batch[0])
        assertContentEquals(IOType.d0(2f), batch[1])
        assertContentEquals(IOType.d0(3f), batch[2])
    }
}

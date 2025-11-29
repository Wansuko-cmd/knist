@file:Suppress("NonAsciiCharacters")

package com.wsr.batch.reshape.fold

import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import kotlin.test.Test
import kotlin.test.assertEquals

class D3ExtTest {
    @Test
    fun `unfold_D3版のim2col変換`() {
        // 入力: [1] x [1, 3, 3] (channel=1, sizeX=3, sizeY=3)
        // windowSize=2, stride=1, padding=0
        // 出力サイズ: (3-2+0)/1+1 = 2x2
        val input = batchOf(
            IOType.d3(1, 3, 3) { _, x, y -> (x * 3 + y + 1).toFloat() },
        )
        // input[0, x, y] = x * 3 + y + 1
        // input[0, 0, 0] = 1, input[0, 1, 0] = 4, input[0, 2, 0] = 7
        // input[0, 0, 1] = 2, input[0, 1, 1] = 5, input[0, 2, 1] = 8
        // input[0, 0, 2] = 3, input[0, 1, 2] = 6, input[0, 2, 2] = 9

        val col = input.unfold(windowSize = 2, stride = 1, padding = 0)

        // 出力形状: [windowSize*windowSize*channel, outputSizeX*outputSizeY*batchSize]
        // = [2*2*1, 2*2*1] = [4, 4]
        assertEquals(expected = 4, actual = col.shape[0])
        assertEquals(expected = 4, actual = col.shape[1])

        // col[row, col]のrowは (c * ws * ws + wy * ws + wx)
        // col[row, col]のcolは (b * osX * osY + oy * osX + ox)
        // window (ox=0, oy=0): 左上2x2 -> input[0,0], input[1,0], input[0,1], input[1,1]
        assertEquals(expected = 1f, actual = col[0, 0], "window(0,0)[wx=0,wy=0]") // input[0,0,0] = 1
        assertEquals(expected = 4f, actual = col[1, 0], "window(0,0)[wx=1,wy=0]") // input[0,1,0] = 4
        assertEquals(expected = 2f, actual = col[2, 0], "window(0,0)[wx=0,wy=1]") // input[0,0,1] = 2
        assertEquals(expected = 5f, actual = col[3, 0], "window(0,0)[wx=1,wy=1]") // input[0,1,1] = 5
    }

    @Test
    fun `fold_D3版のcol2im変換`() {
        // unfoldの逆操作
        // input[0, x, y] = x * 3 + y + 1
        val input = batchOf(
            IOType.d3(1, 3, 3) { _, x, y -> (x * 3 + y + 1).toFloat() },
        )

        val col = input.unfold(windowSize = 2, stride = 1, padding = 0)
        val folded = col.fold(
            batchSize = 1,
            channel = 1,
            inputX = 3,
            inputY = 3,
            stride = 1,
            padding = 0,
        )

        assertEquals(expected = 1, actual = folded.size)
        val result = folded[0]
        assertEquals(expected = listOf(1, 3, 3), actual = result.shape)

        // 重複部分は加算される
        // 2x2 windowがstride=1で移動するので:
        // (0,0): 1回, (1,0): 2回, (2,0): 1回
        // (0,1): 2回, (1,1): 4回, (2,1): 2回
        // (0,2): 1回, (1,2): 2回, (2,2): 1回
        // input[c,x,y] = x * 3 + y + 1
        assertEquals(expected = 1f * 1, actual = result[0, 0, 0]) // (0,0) = 1, 1回
        assertEquals(expected = 4f * 2, actual = result[0, 1, 0]) // (1,0) = 4, 2回
        assertEquals(expected = 7f * 1, actual = result[0, 2, 0]) // (2,0) = 7, 1回
        assertEquals(expected = 2f * 2, actual = result[0, 0, 1]) // (0,1) = 2, 2回
        assertEquals(expected = 5f * 4, actual = result[0, 1, 1]) // (1,1) = 5, 4回
        assertEquals(expected = 8f * 2, actual = result[0, 2, 1]) // (2,1) = 8, 2回
        assertEquals(expected = 3f * 1, actual = result[0, 0, 2]) // (0,2) = 3, 1回
        assertEquals(expected = 6f * 2, actual = result[0, 1, 2]) // (1,2) = 6, 2回
        assertEquals(expected = 9f * 1, actual = result[0, 2, 2]) // (2,2) = 9, 1回
    }

    @Test
    fun `unfold_stride2でのim2col変換`() {
        // 入力: [1] x [1, 4, 4]
        // windowSize=2, stride=2, padding=0
        // 出力サイズ: (4-2+0)/2+1 = 2x2
        val input = batchOf(
            IOType.d3(1, 4, 4) { _, x, y -> (x * 4 + y + 1).toFloat() },
        )

        val col = input.unfold(windowSize = 2, stride = 2, padding = 0)

        assertEquals(expected = 4, actual = col.shape[0]) // 2*2*1
        assertEquals(expected = 4, actual = col.shape[1]) // 2*2*1
    }

    @Test
    fun `unfold_複数チャンネルでのim2col変換`() {
        // 入力: [1] x [2, 3, 3] (channel=2)
        // windowSize=2, stride=1, padding=0
        val input = batchOf(
            IOType.d3(2, 3, 3) { c, x, y -> (c * 9 + x * 3 + y + 1).toFloat() },
        )

        val col = input.unfold(windowSize = 2, stride = 1, padding = 0)

        // 出力形状: [2*2*2, 2*2*1] = [8, 4]
        assertEquals(expected = 8, actual = col.shape[0])
        assertEquals(expected = 4, actual = col.shape[1])
    }

    @Test
    fun `unfold_バッチサイズ2でのim2col変換`() {
        // 入力: [2] x [1, 3, 3]
        val input = batchOf(
            IOType.d3(1, 3, 3) { _, x, y -> (x * 3 + y + 1).toFloat() },
            IOType.d3(1, 3, 3) { _, x, y -> (x * 3 + y + 10).toFloat() },
        )

        val col = input.unfold(windowSize = 2, stride = 1, padding = 0)

        // 出力形状: [4, 8] (4 windows * 2 batches)
        assertEquals(expected = 4, actual = col.shape[0])
        assertEquals(expected = 8, actual = col.shape[1])
    }
}

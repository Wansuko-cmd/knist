@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class ConvD1ExtTest {
    @Test
    fun `D1のconvD1=1次元畳み込み_stride1_padding0`() {
        // [1, 2, 3, 4, 5]
        val input = IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f))
        // [1, 0, -1]
        val filter = IOType.d1(listOf(1.0f, 0.0f, -1.0f))

        val result = input.convD1(filter, stride = 1, padding = 0)

        // output size = (5 - 3 + 0) / 1 + 1 = 3
        // 位置0: 1*1 + 2*0 + 3*(-1) = -2
        // 位置1: 2*1 + 3*0 + 4*(-1) = -2
        // 位置2: 3*1 + 4*0 + 5*(-1) = -2
        assertEquals(
            expected = IOType.d1(listOf(-2.0f, -2.0f, -2.0f)),
            actual = result,
        )
    }

    @Test
    fun `D1のconvD1=1次元畳み込み_stride2_padding0`() {
        // [1, 2, 3, 4, 5]
        val input = IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f))
        // [1, 1]
        val filter = IOType.d1(listOf(1.0f, 1.0f))

        val result = input.convD1(filter, stride = 2, padding = 0)

        // output size = (5 - 2 + 0) / 2 + 1 = 2
        // 位置0: 1*1 + 2*1 = 3
        // 位置1: 3*1 + 4*1 = 7
        assertEquals(
            expected = IOType.d1(listOf(3.0f, 7.0f)),
            actual = result,
        )
    }

    @Test
    fun `D1のconvD1=1次元畳み込み_stride1_padding1`() {
        // [1, 2, 3]
        val input = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        // [1, 1, 1]
        val filter = IOType.d1(listOf(1.0f, 1.0f, 1.0f))

        val result = input.convD1(filter, stride = 1, padding = 1)

        // output size = (3 - 3 + 2) / 1 + 1 = 3
        // 位置0: 0*1 + 1*1 + 2*1 = 3 (左padding)
        // 位置1: 1*1 + 2*1 + 3*1 = 6
        // 位置2: 2*1 + 3*1 + 0*1 = 5 (右padding)
        assertEquals(
            expected = IOType.d1(listOf(3.0f, 6.0f, 5.0f)),
            actual = result,
        )
    }

    @Test
    fun `D1のdeConvD1=1次元逆畳み込み`() {
        // [1, 2]
        val input = IOType.d1(listOf(1.0f, 2.0f))
        // [1, 1]
        val filter = IOType.d1(listOf(1.0f, 1.0f))

        val result = input.deConvD1(filter, stride = 2, padding = 0)

        // stride padding後: [1, 0, 2]
        // padding追加: [0, 1, 0, 2, 0]
        // 畳み込み (kernel=2): output size = 5 - 2 + 1 = 4
        // [1, 1, 2, 2]
        assertEquals(
            expected = IOType.d1(listOf(1.0f, 1.0f, 2.0f, 2.0f)),
            actual = result,
        )
    }

    @Test
    fun `List_D2のconvD1=バッチ対応版1次元畳み込み`() {
        // バッチサイズ2, チャネル2, 入力サイズ8
        // batch0: [[1, 2, 3, 4, 5, 6, 7, 8],
        //          [2, 3, 4, 5, 6, 7, 8, 9]]
        // batch1: [[3, 4, 5, 6, 7, 8, 9, 10],
        //          [4, 5, 6, 7, 8, 9, 10, 11]]
        val input =
            listOf(
                IOType.d2(2, 8) { c, y -> (c + y + 1).toFloat() },
                IOType.d2(2, 8) { c, y -> (c + y + 3).toFloat() },
            )
        // フィルタ数2, チャネル2, カーネル3
        // filter0: [[1, 0, -1], [1, 0, -1]]
        // filter1: [[0, 1, 0], [0, 1, 0]]
        val weight =
            IOType.d3(2, 2, 3) { f, _, k ->
                when {
                    f == 0 && k == 0 -> 1.0f
                    f == 0 && k == 2 -> -1.0f
                    f == 1 && k == 1 -> 1.0f
                    else -> 0.0f
                }
            }

        val result = input.convD1(weight, stride = 1, padding = 0)

        // output size = (8 - 3 + 0) / 1 + 1 = 6
        assertEquals(expected = 2, actual = result.size)

        // batch0, filter0の結果を検証
        // 位置0: (1*1 + 2*0 + 3*(-1)) + (2*1 + 3*0 + 4*(-1)) = -2 + -2 = -4
        assertEquals(expected = -4.0f, actual = result[0][0, 0])

        // batch0, filter1の結果を検証
        // 位置0: (1*0 + 2*1 + 3*0) + (2*0 + 3*1 + 4*0) = 2 + 3 = 5
        assertEquals(expected = 5.0f, actual = result[0][1, 0])
    }

    @Test
    fun `List_D2のdeConvD1=バッチ対応版1次元逆畳み込み`() {
        // バッチサイズ2, チャネル2, 入力サイズ3
        // batch0: [[1, 2, 3],
        //          [2, 3, 4]]
        // batch1: [[3, 4, 5],
        //          [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { c, y -> (c + y + 1).toFloat() },
                IOType.d2(2, 3) { c, y -> (c + y + 3).toFloat() },
            )
        // フィルタ数2, チャネル2, カーネル3
        // filter0: [[1, 1, 1], [1, 1, 1]]
        // filter1: [[1, 0, -1], [1, 0, -1]]
        val weight =
            IOType.d3(2, 2, 3) { f, _, k ->
                when {
                    f == 0 -> 1.0f
                    f == 1 && k == 0 -> 1.0f
                    f == 1 && k == 2 -> -1.0f
                    else -> 0.0f
                }
            }

        val result = input.deConvD1(weight, stride = 2, padding = 0)

        // stride padding後: [[1, 0, 2, 0, 3], [2, 0, 3, 0, 4]]
        // padding追加 (kernel-padding-1 = 3-0-1 = 2): [[0, 0, 1, 0, 2, 0, 3, 0, 0], ...]
        // 畳み込み (kernel=3): output size = 9 - 3 + 1 = 7
        assertEquals(expected = 2, actual = result.size)

        // batch0, filter0, 位置0の結果を検証
        // channel0: 0*1 + 0*1 + 1*1 = 1
        // channel1: 0*1 + 0*1 + 2*1 = 2
        // 合計: 1 + 2 = 3
        assertEquals(expected = 3.0f, actual = result[0][0, 0])

        // batch1, filter0, 位置0の結果を検証
        // channel0: 0*1 + 0*1 + 3*1 = 3
        // channel1: 0*1 + 0*1 + 4*1 = 4
        // 合計: 3 + 4 = 7
        assertEquals(expected = 7.0f, actual = result[1][0, 0])
    }
}

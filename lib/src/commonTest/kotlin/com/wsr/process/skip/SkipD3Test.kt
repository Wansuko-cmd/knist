@file:Suppress("NonAsciiCharacters")

package com.wsr.process.skip

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import com.wsr.process.bias.BiasD3
import com.wsr.process.function.linear.LinearD3
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipD3Test {
    @Test
    fun `SkipD3の_expect=サブ層を通した結果と入力を足す`() {
        // サブ層1: Bias([[[1, 2, 3], [4, 5, 6]]])
        val bias = BiasD3(
            outputX = 1,
            outputY = 2,
            outputZ = 3,
            optimizer = Sgd(0.1).d3(1, 2, 3),
            weight = IOType.d3(1, 2, 3) { x, y, z -> (y * 3 + z + 1).toDouble() },
        )

        // サブ層2: Linear (恒等変換)
        val linear = LinearD3(
            outputX = 1,
            outputY = 2,
            outputZ = 3,
        )

        val skip = SkipD3(
            layers = listOf(bias, linear),
            outputX = 1,
            outputY = 2,
            outputZ = 3,
        )

        // input = [[[10, 20, 30], [40, 50, 60]]]
        val input = listOf(IOType.d3(1, 2, 3) { x, y, z -> ((y * 3 + z + 1) * 10).toDouble() })

        // サブ層1 (bias): [[[10, 20, 30], [40, 50, 60]]] + [[[1, 2, 3], [4, 5, 6]]]
        //               = [[[11, 22, 33], [44, 55, 66]]]
        // サブ層2 (linear): [[[11, 22, 33], [44, 55, 66]]] = [[[11, 22, 33], [44, 55, 66]]]
        // skip出力: [[[10, 20, 30], [40, 50, 60]]] + [[[11, 22, 33], [44, 55, 66]]]
        //         = [[[21, 42, 63], [84, 105, 126]]]
        val result = skip._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        assertEquals(expected = 21.0, actual = output[0, 0, 0])
        assertEquals(expected = 42.0, actual = output[0, 0, 1])
        assertEquals(expected = 63.0, actual = output[0, 0, 2])
        assertEquals(expected = 84.0, actual = output[0, 1, 0])
        assertEquals(expected = 105.0, actual = output[0, 1, 1])
        assertEquals(expected = 126.0, actual = output[0, 1, 2])
    }

    @Test
    fun `SkipD3の_train=skip pathとmain pathの勾配を足して返す`() {
        // サブ層: Bias([[[0, 0], [0, 0]]]) - 恒等変換
        val biasWeight = IOType.d3(1, 2, 2) { _, _, _ -> 0.0 }
        val biasLayer = BiasD3(
            outputX = 1,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(0.1).d3(1, 2, 2),
            weight = biasWeight,
        )

        val skip = SkipD3(
            layers = listOf(biasLayer),
            outputX = 1,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]]]
        val input = listOf(IOType.d3(1, 2, 2) { x, y, z -> (y * 2 + z + 1).toDouble() })

        // 次の層からのdelta = [[[10, 20], [30, 40]]]
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(1, 2, 2) { x, y, z -> ((y * 2 + z + 1) * 10).toDouble() })
        }

        val result = skip._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3

        // skip pathの勾配: [[[10, 20], [30, 40]]] (deltaがそのまま流れる)
        // main pathの勾配: [[[10, 20], [30, 40]]] (biasは勾配をそのまま返す)
        // 合計: [[[20, 40], [60, 80]]]
        assertEquals(expected = 20.0, actual = dx[0, 0, 0])
        assertEquals(expected = 40.0, actual = dx[0, 0, 1])
        assertEquals(expected = 60.0, actual = dx[0, 1, 0])
        assertEquals(expected = 80.0, actual = dx[0, 1, 1])
    }

    @Test
    fun `SkipD3の_train=サブ層の重みが正しく更新される`() {
        // サブ層1: Linear - 恒等変換
        val linearLayer = LinearD3(
            outputX = 2,
            outputY = 2,
            outputZ = 1,
        )

        // サブ層2: Bias([[[1], [1]], [[1], [1]]])
        val biasWeight = IOType.d3(2, 2, 1) { _, _, _ -> 1.0 }
        val biasLayer = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 1,
            optimizer = Sgd(0.1).d3(2, 2, 1),
            weight = biasWeight,
        )

        val skip = SkipD3(
            layers = listOf(linearLayer, biasLayer),
            outputX = 2,
            outputY = 2,
            outputZ = 1,
        )

        // input = [[[2], [3]], [[4], [5]]]
        val input = listOf(IOType.d3(2, 2, 1) { x, y, z -> (x * 2 + y + 2).toDouble() })

        // 次の層からのdelta = [[[1], [2]], [[3], [4]]]
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 1) { x, y, z -> (x * 2 + y + 1).toDouble() })
        }

        // train実行前
        // linear出力: [[[2], [3]], [[4], [5]]]
        // bias出力: [[[2], [3]], [[4], [5]]] + [[[1], [1]], [[1], [1]]]
        //         = [[[3], [4]], [[5], [6]]]
        // skip出力: [[[2], [3]], [[4], [5]]] + [[[3], [4]], [[5], [6]]]
        //         = [[[5], [7]], [[9], [11]]]

        skip._train(input, calcDelta)

        // train実行後の期待値
        // bias更新: [[[1], [1]], [[1], [1]]] - 0.1 * [[[1], [2]], [[3], [4]]]
        //        = [[[0.9], [0.8]], [[0.7], [0.6]]]

        // 更新後のexpect
        // linear出力: [[[2], [3]], [[4], [5]]]
        // bias出力: [[[2], [3]], [[4], [5]]] + [[[0.9], [0.8]], [[0.7], [0.6]]]
        //         = [[[2.9], [3.8]], [[4.7], [5.6]]]
        // skip出力: [[[2], [3]], [[4], [5]]] + [[[2.9], [3.8]], [[4.7], [5.6]]]
        //         = [[[4.9], [6.8]], [[8.7], [10.6]]]
        val afterOutput = skip._expect(input)[0] as IOType.D3

        assertEquals(expected = 4.9, actual = afterOutput[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 6.8, actual = afterOutput[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 8.7, actual = afterOutput[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 10.6, actual = afterOutput[1, 1, 0], absoluteTolerance = 1e-10)
    }
}

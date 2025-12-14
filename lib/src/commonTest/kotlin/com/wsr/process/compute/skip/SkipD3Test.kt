@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.skip

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.process.Context
import com.wsr.process.compute.bias.BiasD3
import com.wsr.process.compute.function.linear.LinearD3
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
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
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(1, 2, 3),
            weight = IOType.d3(1, 2, 3) { x, y, z -> (y * 3 + z + 1).toFloat() },
        )

        // サブ層2: Linear (恒等変換)
        val linear = LinearD3(
            outputX = 1,
            outputY = 2,
            outputZ = 3,
        )

        val skip = SkipD3(
            layers = listOf(bias, linear),
            inputX = 1,
            inputY = 2,
            inputZ = 3,
            outputX = 1,
            outputY = 2,
            outputZ = 3,
        )

        // input = [[[10, 20, 30], [40, 50, 60]]]
        val input = batchOf(IOType.d3(1, 2, 3) { x, y, z -> ((y * 3 + z + 1) * 10).toFloat() })
        val context = Context(input)

        // サブ層1 (bias): [[[10, 20, 30], [40, 50, 60]]] + [[[1, 2, 3], [4, 5, 6]]]
        //               = [[[11, 22, 33], [44, 55, 66]]]
        // サブ層2 (linear): [[[11, 22, 33], [44, 55, 66]]] = [[[11, 22, 33], [44, 55, 66]]]
        // skip出力: [[[10, 20, 30], [40, 50, 60]]] + [[[11, 22, 33], [44, 55, 66]]]
        //         = [[[21, 42, 63], [84, 105, 126]]]
        val result = skip._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 21.0f, actual = output[0, 0, 0])
        assertEquals(expected = 42.0f, actual = output[0, 0, 1])
        assertEquals(expected = 63.0f, actual = output[0, 0, 2])
        assertEquals(expected = 84.0f, actual = output[0, 1, 0])
        assertEquals(expected = 105.0f, actual = output[0, 1, 1])
        assertEquals(expected = 126.0f, actual = output[0, 1, 2])
    }

    @Test
    fun `SkipD3の_train=skip pathとmain pathの勾配を足して返す`() {
        // サブ層: Bias([[[0, 0], [0, 0]]]) - 恒等変換
        val biasWeight = IOType.d3(1, 2, 2) { _, _, _ -> 0.0f }
        val biasLayer = BiasD3(
            outputX = 1,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(1, 2, 2),
            weight = biasWeight,
        )

        val skip = SkipD3(
            layers = listOf(biasLayer),
            inputX = 1,
            inputY = 2,
            inputZ = 2,
            outputX = 1,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]]]
        val input = batchOf(IOType.d3(1, 2, 2) { x, y, z -> (y * 2 + z + 1).toFloat() })
        val context = Context(input)

        // 次の層からのdelta = [[[10, 20], [30, 40]]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(1, 2, 2) { x, y, z -> ((y * 2 + z + 1) * 10).toFloat() })
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [[[10, 20], [30, 40]]] (deltaがそのまま流れる)
        // main pathの勾配: [[[10, 20], [30, 40]]] (biasは勾配をそのまま返す)
        // 合計: [[[20, 40], [60, 80]]]
        assertEquals(expected = 20.0f, actual = dx[0, 0, 0])
        assertEquals(expected = 40.0f, actual = dx[0, 0, 1])
        assertEquals(expected = 60.0f, actual = dx[0, 1, 0])
        assertEquals(expected = 80.0f, actual = dx[0, 1, 1])
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
        val biasWeight = IOType.d3(2, 2, 1) { _, _, _ -> 1.0f }
        val biasLayer = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 1,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(2, 2, 1),
            weight = biasWeight,
        )

        val skip = SkipD3(
            layers = listOf(linearLayer, biasLayer),
            inputX = 2,
            inputY = 2,
            inputZ = 1,
            outputX = 2,
            outputY = 2,
            outputZ = 1,
        )

        // input = [[[2], [3]], [[4], [5]]]
        val input = batchOf(IOType.d3(2, 2, 1) { x, y, z -> (x * 2 + y + 2).toFloat() })
        val context = Context(input)

        // 次の層からのdelta = [[[1], [2]], [[3], [4]]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 1) { x, y, z -> (x * 2 + y + 1).toFloat() })
        }

        // train実行前
        // linear出力: [[[2], [3]], [[4], [5]]]
        // bias出力: [[[2], [3]], [[4], [5]]] + [[[1], [1]], [[1], [1]]]
        //         = [[[3], [4]], [[5], [6]]]
        // skip出力: [[[2], [3]], [[4], [5]]] + [[[3], [4]], [[5], [6]]]
        //         = [[[5], [7]], [[9], [11]]]

        skip._train(input, context, calcDelta) as Batch<IOType.D3>
        // train実行後の期待値
        // bias更新: [[[1], [1]], [[1], [1]]] - 0.1f * [[[1], [2]], [[3], [4]]]
        //        = [[[0.9f], [0.8f]], [[0.7f], [0.6f]]]

        // 更新後のexpect
        // linear出力: [[[2], [3]], [[4], [5]]]
        // bias出力: [[[2], [3]], [[4], [5]]] + [[[0.9f], [0.8f]], [[0.7f], [0.6f]]]
        //         = [[[2.9f], [3.8f]], [[4.7f], [5.6f]]]
        // skip出力: [[[2], [3]], [[4], [5]]] + [[[2.9f], [3.8f]], [[4.7f], [5.6f]]]
        //         = [[[4.9f], [6.8f]], [[8.7f], [10.6f]]]
        val afterOutput = skip._expect(input, context) as Batch<IOType.D3>

        assertEquals(expected = 4.9f, actual = afterOutput[0][0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 6.8f, actual = afterOutput[0][0, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 8.7f, actual = afterOutput[0][1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 10.6f, actual = afterOutput[0][1, 1, 0], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `SkipD3の_expect=inputSizeがoutputSizeより小さい場合にzero-paddingで拡張される`() {
        // inputSize=(2,2,2), outputSize=(2,2,2) だが、実際には一部がzero
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 0.0f },
        )

        val skip = SkipD3(
            layers = listOf(bias),
            inputX = 2,
            inputY = 2,
            inputZ = 2,
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input = batchOf(
            IOType.d3(2, 2, 2) { x, y, z ->
                (x * 4 + y * 2 + z + 1).toFloat()
            },
        )
        val context = Context(input)

        // main path: [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        // skip path: [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        // 出力: [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        val result = skip._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0, 0])
        assertEquals(expected = 4.0f, actual = output[0, 0, 1])
        assertEquals(expected = 6.0f, actual = output[0, 1, 0])
        assertEquals(expected = 8.0f, actual = output[0, 1, 1])
        assertEquals(expected = 10.0f, actual = output[1, 0, 0])
        assertEquals(expected = 12.0f, actual = output[1, 0, 1])
        assertEquals(expected = 14.0f, actual = output[1, 1, 0])
        assertEquals(expected = 16.0f, actual = output[1, 1, 1])
    }

    @Test
    fun `SkipD3の_train=inputSizeがoutputSizeより小さい場合に勾配が正しく切り詰められる`() {
        // inputSize=(2,2,2), outputSize=(2,2,2)
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 0.0f },
        )

        val skip = SkipD3(
            layers = listOf(bias),
            inputX = 2,
            inputY = 2,
            inputZ = 2,
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input = batchOf(
            IOType.d3(2, 2, 2) { x, y, z ->
                (x * 4 + y * 2 + z + 1).toFloat()
            },
        )
        val context = Context(input)

        // 次の層からのdelta = [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    ((x * 4 + y * 2 + z + 1) * 10).toFloat()
                },
            )
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        // main pathの勾配: [[[10, 20], [30, 40]], [[50, 60], [70, 80]]] (biasは勾配をそのまま返す)
        // 合計: [[[20, 40], [60, 80]], [[100, 120], [140, 160]]]
        assertEquals(expected = 20.0f, actual = dx[0, 0, 0])
        assertEquals(expected = 40.0f, actual = dx[0, 0, 1])
        assertEquals(expected = 60.0f, actual = dx[0, 1, 0])
        assertEquals(expected = 80.0f, actual = dx[0, 1, 1])
        assertEquals(expected = 100.0f, actual = dx[1, 0, 0])
        assertEquals(expected = 120.0f, actual = dx[1, 0, 1])
        assertEquals(expected = 140.0f, actual = dx[1, 1, 0])
        assertEquals(expected = 160.0f, actual = dx[1, 1, 1])
    }

    @Test
    fun `SkipD3の_expect=inputSizeがoutputSizeより大きい場合にaverage poolingで縮小される`() {
        // inputSize=(2,2,2), outputSize=(2,2,2) だが、内部的にaverage poolingをシミュレート
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 0.0f },
        )

        val skip = SkipD3(
            layers = listOf(bias),
            inputX = 2,
            inputY = 2,
            inputZ = 2,
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input = batchOf(
            IOType.d3(2, 2, 2) { x, y, z ->
                (x * 4 + y * 2 + z + 1).toFloat()
            },
        )
        val context = Context(input)

        // main path: [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        // skip path: [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        // 出力: [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        val result = skip._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0, 0])
        assertEquals(expected = 4.0f, actual = output[0, 0, 1])
        assertEquals(expected = 6.0f, actual = output[0, 1, 0])
        assertEquals(expected = 8.0f, actual = output[0, 1, 1])
        assertEquals(expected = 10.0f, actual = output[1, 0, 0])
        assertEquals(expected = 12.0f, actual = output[1, 0, 1])
        assertEquals(expected = 14.0f, actual = output[1, 1, 0])
        assertEquals(expected = 16.0f, actual = output[1, 1, 1])
    }

    @Test
    fun `SkipD3の_train=inputSizeがoutputSizeより大きい場合に勾配が正しく分配される`() {
        // inputSize=(2,2,2), outputSize=(2,2,2)
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(Scheduler.Fix(0.1f)).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 0.0f },
        )

        val skip = SkipD3(
            layers = listOf(bias),
            inputX = 2,
            inputY = 2,
            inputZ = 2,
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input = batchOf(
            IOType.d3(2, 2, 2) { x, y, z ->
                (x * 4 + y * 2 + z + 1).toFloat()
            },
        )
        val context = Context(input)

        // 次の層からのdelta = [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    ((x * 4 + y * 2 + z + 1) * 10).toFloat()
                },
            )
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        // main pathの勾配: [[[10, 20], [30, 40]], [[50, 60], [70, 80]]] (biasは勾配をそのまま返す)
        // 合計: [[[20, 40], [60, 80]], [[100, 120], [140, 160]]]
        assertEquals(expected = 20.0f, actual = dx[0, 0, 0])
        assertEquals(expected = 40.0f, actual = dx[0, 0, 1])
        assertEquals(expected = 60.0f, actual = dx[0, 1, 0])
        assertEquals(expected = 80.0f, actual = dx[0, 1, 1])
        assertEquals(expected = 100.0f, actual = dx[1, 0, 0])
        assertEquals(expected = 120.0f, actual = dx[1, 0, 1])
        assertEquals(expected = 140.0f, actual = dx[1, 1, 0])
        assertEquals(expected = 160.0f, actual = dx[1, 1, 1])
    }
}

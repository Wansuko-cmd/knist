@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.skip

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.process.affine.AffineD1
import com.wsr.layer.process.bias.BiasD1
import com.wsr.layer.process.skip.SkipD1
import com.wsr.optimizer.sgd.Sgd
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipD1Test {
    @Test
    fun `SkipD1の_expect=サブ層を通した結果と入力を足す`() {
        // サブ層1: Bias([1, 2])
        val bias = BiasD1(
            outputSize = 2,
            optimizer = Sgd(0.1f).d1(size = 2),
            weight = IOType.d1(listOf(1.0f, 2.0f)),
        )

        // サブ層2: Affine([[1, 0], [0, 1]]) (恒等行列)
        val affine = AffineD1(
            outputSize = 2,
            optimizer = Sgd(0.1f).d2(2, 2),
            weight = IOType.d2(2, 2) { x, y -> if (x == y) 1.0f else 0.0f },
        )

        val skip = SkipD1(
            layers = listOf(bias, affine),
            inputSize = 2,
            outputSize = 2,
        )

        // input = [10, 20]
        val input = batchOf(IOType.d1(listOf(10.0f, 20.0f)))
        val context = Context(input)

        // サブ層1 (bias): [10, 20] + [1, 2] = [11, 22]
        // サブ層2 (affine): [[1,0],[0,1]]^T dot [11, 22] = [11, 22]
        // skip出力: [10, 20] + [11, 22] = [21, 42]
        val result = skip._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 21.0f, actual = output[0])
        assertEquals(expected = 42.0f, actual = output[1])
    }

    @Test
    fun `SkipD1の_train=skip pathとmain pathの勾配を足して返す`() {
        // サブ層: Bias([0, 0, 0]) - 恒等変換
        val biasWeight = IOType.d1(listOf(0.0f, 0.0f, 0.0f))
        val biasLayer = BiasD1(
            outputSize = 3,
            optimizer = Sgd(0.1f).d1(size = 3),
            weight = biasWeight,
        )

        val skip = SkipD1(
            layers = listOf(biasLayer),
            inputSize = 3,
            outputSize = 3,
        )

        // input = [1, 2, 3]
        val input = batchOf(IOType.d1(listOf(1.0f, 2.0f, 3.0f)))
        val context = Context(input)

        // 次の層からのdelta = [10, 20, 30]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(10.0f, 20.0f, 30.0f)))
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [10, 20, 30] (deltaがそのまま流れる)
        // main pathの勾配: [10, 20, 30] (biasは勾配をそのまま返す)
        // 合計: [20, 40, 60]
        assertEquals(expected = 20.0f, actual = dx[0])
        assertEquals(expected = 40.0f, actual = dx[1])
        assertEquals(expected = 60.0f, actual = dx[2])
    }

    @Test
    fun `SkipD1の_train=サブ層の重みが正しく更新される`() {
        // サブ層1: Affine([[1, 0], [0, 1]]) - 恒等行列
        val affineWeight = IOType.d2(2, 2) { x, y -> if (x == y) 1.0f else 0.0f }
        val affineLayer = AffineD1(
            outputSize = 2,
            optimizer = Sgd(0.1f).d2(2, 2),
            weight = affineWeight,
        )

        // サブ層2: Bias([1, 1])
        val biasWeight = IOType.d1(listOf(1.0f, 1.0f))
        val biasLayer = BiasD1(
            outputSize = 2,
            optimizer = Sgd(0.1f).d1(size = 2),
            weight = biasWeight,
        )

        val skip = SkipD1(
            layers = listOf(affineLayer, biasLayer),
            inputSize = 2,
            outputSize = 2,
        )

        // input = [2, 3]
        val input = batchOf(IOType.d1(listOf(2.0f, 3.0f)))
        val context = Context(input)

        // 次の層からのdelta = [1, 2]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(1.0f, 2.0f)))
        }

        // train実行前
        // affine出力: [[1,0],[0,1]]^T dot [2, 3] = [2, 3]
        // bias出力: [2, 3] + [1, 1] = [3, 4]
        // skip出力: [2, 3] + [3, 4] = [5, 7]

        skip._train(input, context, calcDelta) as Batch<IOType.D1>
        // train実行後の期待値
        // bias更新: [1, 1] - 0.1f * [1, 2] = [0.9f, 0.8f]
        // affine更新: [[1,0],[0,1]] - 0.1f * [[2],[3]] dot [[1,2]]
        //           = [[1,0],[0,1]] - [[0.2f,0.4f],[0.3f,0.6f]]
        //           = [[0.8f,-0.4f],[-0.3f,0.4f]]

        // 更新後のexpect
        // affine出力: [[0.8f,-0.3f],[-0.4f,0.4f]] dot [2, 3] = [0.7f, 0.4f]
        // bias出力: [0.7f, 0.4f] + [0.9f, 0.8f] = [1.6f, 1.2f]
        // skip出力: [2, 3] + [1.6f, 1.2f] = [3.6f, 4.2f]
        val afterOutput = skip._expect(input, context) as Batch<IOType.D1>

        assertEquals(
            expected = 3.6f,
            actual = afterOutput[0][0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 4.2f,
            actual = afterOutput[0][1],
            absoluteTolerance = 1e-6f,
        )
    }

    @Test
    fun `SkipD1の_expect=inputSizeがoutputSizeより小さい場合にzero-paddingで拡張される`() {
        // inputSize=2, outputSize=3
        // サブ層: Affine([[1, 0], [0, 1], [0, 0]]) - 2次元を3次元に変換
        val affine = AffineD1(
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(2, 3),
            weight = IOType.d2(2, 3) { x, y -> if (x == y) 1.0f else 0.0f },
        )

        val skip = SkipD1(
            layers = listOf(affine),
            inputSize = 2,
            outputSize = 3,
        )

        // input = [10, 20]
        val input = batchOf(IOType.d1(listOf(10.0f, 20.0f)))
        val context = Context(input)

        // main path: Affine([[1,0,0],[0,1,0]]^T) dot [10, 20] = [10, 20, 0]
        // skip path: [10, 20, 0] (zero-padding)
        // 出力: [10, 20, 0] + [10, 20, 0] = [20, 40, 0]
        val result = skip._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 20.0f, actual = output[0])
        assertEquals(expected = 40.0f, actual = output[1])
        assertEquals(expected = 0.0f, actual = output[2])
    }

    @Test
    fun `SkipD1の_train=inputSizeがoutputSizeより小さい場合に勾配が正しく切り詰められる`() {
        // inputSize=2, outputSize=3
        // サブ層: Affine([[1, 0], [0, 1], [0, 0]]) - 恒等変換的に2->3次元変換
        val affine = AffineD1(
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(2, 3),
            weight = IOType.d2(2, 3) { x, y -> if (x == y) 1.0f else 0.0f },
        )

        val skip = SkipD1(
            layers = listOf(affine),
            inputSize = 2,
            outputSize = 3,
        )

        // input = [1, 2]
        val input = batchOf(IOType.d1(listOf(1.0f, 2.0f)))
        val context = Context(input)

        // 次の層からのdelta = [10, 20, 30]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(10.0f, 20.0f, 30.0f)))
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [10, 20, 30] -> [10, 20] (最初の2要素のみ取る)
        // main pathの勾配: Affine^Tを通過
        //   [[1,0,0],[0,1,0]] dot [10, 20, 30] = [10, 20]
        // 合計: [10, 20] + [10, 20] = [20, 40]
        assertEquals(expected = 20.0f, actual = dx[0])
        assertEquals(expected = 40.0f, actual = dx[1])
    }

    @Test
    fun `SkipD1の_expect=inputSizeがoutputSizeより大きい場合にaverage poolingで縮小される`() {
        // inputSize=6, outputSize=3, stride=2
        // サブ層: Affine([[1,0,0,0,0,0],[0,1,0,0,0,0],[0,0,1,0,0,0]]) - 最初の3要素のみ取る
        val affine = AffineD1(
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(6, 3),
            weight = IOType.d2(6, 3) { x, y -> if (x == y) 1.0f else 0.0f },
        )

        val skip = SkipD1(
            layers = listOf(affine),
            inputSize = 6,
            outputSize = 3,
        )

        // input = [1, 2, 3, 4, 5, 6]
        val input = batchOf(IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)))
        val context = Context(input)

        // main path: Affine -> [1, 2, 3]
        // skip path (average pooling):
        //   [1, 2]の平均 = 1.5f
        //   [3, 4]の平均 = 3.5f
        //   [5, 6]の平均 = 5.5f
        //   -> [1.5f, 3.5f, 5.5f]
        // 出力: [1, 2, 3] + [1.5f, 3.5f, 5.5f] = [2.5f, 5.5f, 8.5f]
        val result = skip._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.5f, actual = output[0])
        assertEquals(expected = 5.5f, actual = output[1])
        assertEquals(expected = 8.5f, actual = output[2])
    }

    @Test
    fun `SkipD1の_train=inputSizeがoutputSizeより大きい場合に勾配が正しく分配される`() {
        // inputSize=6, outputSize=3, stride=2
        // サブ層: Affine([[1,0,0,0,0,0],[0,1,0,0,0,0],[0,0,1,0,0,0]]) - 最初の3要素のみ取る
        val affine = AffineD1(
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(6, 3),
            weight = IOType.d2(6, 3) { x, y -> if (x == y) 1.0f else 0.0f },
        )

        val skip = SkipD1(
            layers = listOf(affine),
            inputSize = 6,
            outputSize = 3,
        )

        // input = [1, 2, 3, 4, 5, 6]
        val input = batchOf(IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)))
        val context = Context(input)

        // 次の層からのdelta = [12, 24, 36]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(12.0f, 24.0f, 36.0f)))
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配 (average poolingの逆伝播):
        //   delta[0] = 12 -> [12/2, 12/2] = [6, 6]
        //   delta[1] = 24 -> [24/2, 24/2] = [12, 12]
        //   delta[2] = 36 -> [36/2, 36/2] = [18, 18]
        //   -> [6, 6, 12, 12, 18, 18]
        // main pathの勾配: [[1,0,0],[0,1,0],[0,0,1],[0,0,0],[0,0,0],[0,0,0]] dot [12, 24, 36] = [12, 24, 36, 0, 0, 0]
        // 合計: [6+12, 6+24, 12+36, 12+0, 18+0, 18+0] = [18, 30, 48, 12, 18, 18]
        assertEquals(expected = 18.0f, actual = dx[0])
        assertEquals(expected = 30.0f, actual = dx[1])
        assertEquals(expected = 48.0f, actual = dx[2])
        assertEquals(expected = 12.0f, actual = dx[3])
        assertEquals(expected = 18.0f, actual = dx[4])
        assertEquals(expected = 18.0f, actual = dx[5])
    }
}

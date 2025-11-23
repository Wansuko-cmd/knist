@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.skip

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.layer.process.affine.AffineD2
import com.wsr.layer.process.bias.BiasD2
import com.wsr.layer.process.skip.SkipD2
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipD2Test {
    @Test
    fun `SkipD2の_expect=サブ層を通した結果と入力を足す`() {
        // サブ層1: Bias([[1, 2]])
        val bias = BiasD2(
            outputX = 1,
            outputY = 2,
            optimizer = Sgd(0.1f).d2(1, 2),
            weight = IOType.d2(1, 2) { x, y -> if (x == 0 && y == 0) 1.0f else 2.0f },
        )

        // サブ層2: Affine (恒等変換: channel=1, outputSize=2)
        val affine = AffineD2(
            channel = 1,
            outputSize = 2,
            optimizer = Sgd(0.1f).d2(2, 2),
            weight = IOType.d2(2, 2) { y, out -> if (y == out) 1.0f else 0.0f },
        )

        val skip = SkipD2(
            layers = listOf(bias, affine),
            inputX = 1,
            inputY = 2,
            outputX = 1,
            outputY = 2,
        )

        // input = [[10, 20]]
        val input = batchOf(IOType.d2(1, 2) { x, y -> if (x == 0 && y == 0) 10.0f else 20.0f })
        val context = Context(input)

        // サブ層1 (bias): [[10, 20]] + [[1, 2]] = [[11, 22]]
        // サブ層2 (affine): 恒等変換 [[11, 22]] = [[11, 22]]
        // skip出力: [[10, 20]] + [[11, 22]] = [[21, 42]]
        val result = skip._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 21.0f, actual = output[0, 0])
        assertEquals(expected = 42.0f, actual = output[0, 1])
    }

    @Test
    fun `SkipD2の_train=skip pathとmain pathの勾配を足して返す`() {
        // サブ層: Bias([[0, 0, 0]]) - 恒等変換
        val biasWeight = IOType.d2(1, 3) { _, _ -> 0.0f }
        val biasLayer = BiasD2(
            outputX = 1,
            outputY = 3,
            optimizer = Sgd(0.1f).d2(1, 3),
            weight = biasWeight,
        )

        val skip = SkipD2(
            layers = listOf(biasLayer),
            inputX = 1,
            inputY = 3,
            outputX = 1,
            outputY = 3,
        )

        // input = [[1, 2, 3]]
        val input = batchOf(IOType.d2(1, 3) { x, y -> (y + 1).toFloat() })
        val context = Context(input)

        // 次の層からのdelta = [[10, 20, 30]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 3) { x, y -> ((y + 1) * 10).toFloat() })
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [[10, 20, 30]] (deltaがそのまま流れる)
        // main pathの勾配: [[10, 20, 30]] (biasは勾配をそのまま返す)
        // 合計: [[20, 40, 60]]
        assertEquals(expected = 20.0f, actual = dx[0, 0])
        assertEquals(expected = 40.0f, actual = dx[0, 1])
        assertEquals(expected = 60.0f, actual = dx[0, 2])
    }

    @Test
    fun `SkipD2の_train=サブ層の重みが正しく更新される`() {
        // サブ層1: Affine (恒等変換: channel=1, outputSize=2)
        val affineWeight = IOType.d2(2, 2) { y, out -> if (y == out) 1.0f else 0.0f }
        val affineLayer = AffineD2(
            channel = 1,
            outputSize = 2,
            optimizer = Sgd(0.1f).d2(2, 2),
            weight = affineWeight,
        )

        // サブ層2: Bias([[1, 1]])
        val biasWeight = IOType.d2(1, 2) { _, _ -> 1.0f }
        val biasLayer = BiasD2(
            outputX = 1,
            outputY = 2,
            optimizer = Sgd(0.1f).d2(1, 2),
            weight = biasWeight,
        )

        val skip = SkipD2(
            layers = listOf(affineLayer, biasLayer),
            inputX = 1,
            inputY = 2,
            outputX = 1,
            outputY = 2,
        )

        // input = [[2, 3]]
        val input = batchOf(IOType.d2(1, 2) { x, y -> if (x == 0 && y == 0) 2.0f else 3.0f })
        val context = Context(input)

        // 次の層からのdelta = [[1, 2]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 2) { x, y -> if (x == 0 && y == 0) 1.0f else 2.0f })
        }

        // train実行前
        // affine出力: 恒等変換 [[2, 3]] = [[2, 3]]
        // bias出力: [[2, 3]] + [[1, 1]] = [[3, 4]]
        // skip出力: [[2, 3]] + [[3, 4]] = [[5, 7]]

        skip._train(input, context, calcDelta) as Batch<IOType.D2>
        // train実行後の期待値
        // bias更新: [[1, 1]] - 0.1f * [[1, 2]] = [[0.9f, 0.8f]]
        // affine更新: weight[y,out] - 0.1f * input[0,y] * delta[0,out]
        //   weight[0,0] = 1.0f - 0.1f * 2.0f * 1.0f = 0.8f
        //   weight[0,1] = 0.0f - 0.1f * 2.0f * 2.0f = -0.4f
        //   weight[1,0] = 0.0f - 0.1f * 3.0f * 1.0f = -0.3f
        //   weight[1,1] = 1.0f - 0.1f * 3.0f * 2.0f = 0.4f

        // 更新後のexpect
        // affine出力: weight^T · input = [[0.8f, -0.3f], [-0.4f, 0.4f]] · [[2], [3]] = [[0.7f], [0.4f]]
        // bias出力: [[0.7f, 0.4f]] + [[0.9f, 0.8f]] = [[1.6f, 1.2f]]
        // skip出力: [[2, 3]] + [[1.6f, 1.2f]] = [[3.6f, 4.2f]]
        val afterOutput = skip._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 3.6f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 4.2f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `SkipD2の_expect=inputSizeがoutputSizeより小さい場合にzero-paddingで拡張される`() {
        // inputSize=(2,2), outputSize=(3,3)
        // サブ層: BiasD2で恒等変換 (重み全部0)
        val bias = BiasD2(
            outputX = 3,
            outputY = 3,
            optimizer = Sgd(0.1f).d2(3, 3),
            weight = IOType.d2(3, 3) { _, _ -> 0.0f },
        )

        val skip = SkipD2(
            layers = listOf(bias),
            inputX = 3,
            inputY = 3,
            outputX = 3,
            outputY = 3,
        )

        // input = [[1, 2, 0], [3, 4, 0], [0, 0, 0]]  (既にzero-paddingされている想定)
        val input = batchOf(
            IOType.d2(3, 3) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.0f
                    x == 0 && y == 1 -> 2.0f
                    x == 1 && y == 0 -> 3.0f
                    x == 1 && y == 1 -> 4.0f
                    else -> 0.0f
                }
            },
        )
        val context = Context(input)

        // main path: Bias([[0,0,0],[0,0,0],[0,0,0]]) -> [[1,2,0],[3,4,0],[0,0,0]]
        // skip path: [[1,2,0],[3,4,0],[0,0,0]]
        // 出力: [[2,4,0],[6,8,0],[0,0,0]]
        val result = skip._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0])
        assertEquals(expected = 4.0f, actual = output[0, 1])
        assertEquals(expected = 0.0f, actual = output[0, 2])
        assertEquals(expected = 6.0f, actual = output[1, 0])
        assertEquals(expected = 8.0f, actual = output[1, 1])
        assertEquals(expected = 0.0f, actual = output[1, 2])
        assertEquals(expected = 0.0f, actual = output[2, 0])
        assertEquals(expected = 0.0f, actual = output[2, 1])
        assertEquals(expected = 0.0f, actual = output[2, 2])
    }

    @Test
    fun `SkipD2の_train=inputSizeがoutputSizeより小さい場合に勾配が正しく切り詰められる`() {
        // inputSize=(3,3), outputSize=(3,3) だが、実質的には(2,2)の情報のみ
        val bias = BiasD2(
            outputX = 3,
            outputY = 3,
            optimizer = Sgd(0.1f).d2(3, 3),
            weight = IOType.d2(3, 3) { _, _ -> 0.0f },
        )

        val skip = SkipD2(
            layers = listOf(bias),
            inputX = 3,
            inputY = 3,
            outputX = 3,
            outputY = 3,
        )

        // input = [[1, 2, 0], [3, 4, 0], [0, 0, 0]]
        val input = batchOf(
            IOType.d2(3, 3) { x, y ->
                when {
                    x == 0 && y == 0 -> 1.0f
                    x == 0 && y == 1 -> 2.0f
                    x == 1 && y == 0 -> 3.0f
                    x == 1 && y == 1 -> 4.0f
                    else -> 0.0f
                }
            },
        )
        val context = Context(input)

        // 次の層からのdelta = [[10,20,30],[40,50,60],[70,80,90]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(3, 3) { x, y ->
                    ((x * 3 + y + 1) * 10).toFloat()
                },
            )
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配: [[10,20,30],[40,50,60],[70,80,90]]
        // main pathの勾配: [[10,20,30],[40,50,60],[70,80,90]] (biasは勾配をそのまま返す)
        // 合計: [[20,40,60],[80,100,120],[140,160,180]]
        assertEquals(expected = 20.0f, actual = dx[0, 0])
        assertEquals(expected = 40.0f, actual = dx[0, 1])
        assertEquals(expected = 60.0f, actual = dx[0, 2])
        assertEquals(expected = 80.0f, actual = dx[1, 0])
        assertEquals(expected = 100.0f, actual = dx[1, 1])
        assertEquals(expected = 120.0f, actual = dx[1, 2])
        assertEquals(expected = 140.0f, actual = dx[2, 0])
        assertEquals(expected = 160.0f, actual = dx[2, 1])
        assertEquals(expected = 180.0f, actual = dx[2, 2])
    }

    @Test
    fun `SkipD2の_expect=inputSizeがoutputSizeより大きい場合にaverage poolingで縮小される`() {
        // inputSize=(2,6), outputSize=(2,3), stride=(1,2)
        // サブ層: Affine - Y方向のサイズ変換 (最初の3要素のみ取る)
        val affine = AffineD2(
            channel = 2,
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(6, 3),
            weight = IOType.d2(6, 3) { y, out -> if (y == out) 1.0f else 0.0f },
        )

        val skip = SkipD2(
            layers = listOf(affine),
            inputX = 2,
            inputY = 6,
            outputX = 2,
            outputY = 3,
        )

        // input = [[1, 2, 3, 4, 5, 6], [7, 8, 9, 10, 11, 12]]
        val input = batchOf(
            IOType.d2(2, 6) { x, y ->
                (x * 6 + y + 1).toFloat()
            },
        )
        val context = Context(input)

        // main path: Affine -> [[1, 2, 3], [7, 8, 9]]
        // skip path (average pooling):
        //   [1, 2]の平均 = 1.5f
        //   [3, 4]の平均 = 3.5f
        //   [5, 6]の平均 = 5.5f
        //   [7, 8]の平均 = 7.5f
        //   [9, 10]の平均 = 9.5f
        //   [11, 12]の平均 = 11.5f
        //   -> [[1.5f, 3.5f, 5.5f], [7.5f, 9.5f, 11.5f]]
        // 出力: [[1, 2, 3], [7, 8, 9]] + [[1.5f, 3.5f, 5.5f], [7.5f, 9.5f, 11.5f]]
        //     = [[2.5f, 5.5f, 8.5f], [14.5f, 17.5f, 20.5f]]
        val result = skip._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.5f, actual = output[0, 0])
        assertEquals(expected = 5.5f, actual = output[0, 1])
        assertEquals(expected = 8.5f, actual = output[0, 2])
        assertEquals(expected = 14.5f, actual = output[1, 0])
        assertEquals(expected = 17.5f, actual = output[1, 1])
        assertEquals(expected = 20.5f, actual = output[1, 2])
    }

    @Test
    fun `SkipD2の_train=inputSizeがoutputSizeより大きい場合に勾配が正しく分配される`() {
        // inputSize=(2,6), outputSize=(2,3), stride=(1,2)
        // サブ層: Affine - Y方向のサイズ変換 (最初の3要素のみ取る)
        val affine = AffineD2(
            channel = 2,
            outputSize = 3,
            optimizer = Sgd(0.1f).d2(6, 3),
            weight = IOType.d2(6, 3) { y, out -> if (y == out) 1.0f else 0.0f },
        )

        val skip = SkipD2(
            layers = listOf(affine),
            inputX = 2,
            inputY = 6,
            outputX = 2,
            outputY = 3,
        )

        // input = [[1, 2, 3, 4, 5, 6], [7, 8, 9, 10, 11, 12]]
        val input = batchOf(
            IOType.d2(2, 6) { x, y ->
                (x * 6 + y + 1).toFloat()
            },
        )
        val context = Context(input)

        // 次の層からのdelta = [[12, 24, 36], [48, 60, 72]]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(2, 3) { x, y ->
                    ((x * 3 + y + 1) * 12).toFloat()
                },
            )
        }

        val result = skip._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // skip pathの勾配 (average poolingの逆伝播):
        //   delta[0,0] = 12 -> [12/2, 12/2] = [6, 6]
        //   delta[0,1] = 24 -> [24/2, 24/2] = [12, 12]
        //   delta[0,2] = 36 -> [36/2, 36/2] = [18, 18]
        //   delta[1,0] = 48 -> [48/2, 48/2] = [24, 24]
        //   delta[1,1] = 60 -> [60/2, 60/2] = [30, 30]
        //   delta[1,2] = 72 -> [72/2, 72/2] = [36, 36]
        //   -> [[6, 6, 12, 12, 18, 18], [24, 24, 30, 30, 36, 36]]
        // main pathの勾配: Affine^Tを通過
        //   [[1,0,0,0,0,0],[0,1,0,0,0,0],[0,0,1,0,0,0]] dot [[12, 24, 36], [48, 60, 72]]
        //   = [[12, 24, 36, 0, 0, 0], [48, 60, 72, 0, 0, 0]]
        // 合計: [[6+12, 6+24, 12+36, 12+0, 18+0, 18+0], [24+48, 24+60, 30+72, 30+0, 36+0, 36+0]]
        //     = [[18, 30, 48, 12, 18, 18], [72, 84, 102, 30, 36, 36]]
        assertEquals(expected = 18.0f, actual = dx[0, 0])
        assertEquals(expected = 30.0f, actual = dx[0, 1])
        assertEquals(expected = 48.0f, actual = dx[0, 2])
        assertEquals(expected = 12.0f, actual = dx[0, 3])
        assertEquals(expected = 18.0f, actual = dx[0, 4])
        assertEquals(expected = 18.0f, actual = dx[0, 5])
        assertEquals(expected = 72.0f, actual = dx[1, 0])
        assertEquals(expected = 84.0f, actual = dx[1, 1])
        assertEquals(expected = 102.0f, actual = dx[1, 2])
        assertEquals(expected = 30.0f, actual = dx[1, 3])
        assertEquals(expected = 36.0f, actual = dx[1, 4])
        assertEquals(expected = 36.0f, actual = dx[1, 5])
    }
}

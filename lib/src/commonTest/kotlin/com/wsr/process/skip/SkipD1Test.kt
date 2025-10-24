@file:Suppress("NonAsciiCharacters")

package com.wsr.process.skip

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import com.wsr.process.affine.AffineD1
import com.wsr.process.bias.BiasD1
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipD1Test {
    @Test
    fun `SkipD1の_expect=サブ層を通した結果と入力を足す`() {
        // サブ層1: Bias([1, 2])
        val bias = BiasD1(
            outputSize = 2,
            optimizer = Sgd(0.1).d1(size = 2),
            weight = IOType.d1(listOf(1.0, 2.0)),
        )

        // サブ層2: Affine([[1, 0], [0, 1]]) (恒等行列)
        val affine = AffineD1(
            outputSize = 2,
            optimizer = Sgd(0.1).d2(2, 2),
            weight = IOType.d2(2, 2) { x, y -> if (x == y) 1.0 else 0.0 },
        )

        val skip = SkipD1(
            layers = listOf(bias, affine),
            outputSize = 2,
        )

        // input = [10, 20]
        val input = listOf(IOType.d1(listOf(10.0, 20.0)))

        // サブ層1 (bias): [10, 20] + [1, 2] = [11, 22]
        // サブ層2 (affine): [[1,0],[0,1]]^T dot [11, 22] = [11, 22]
        // skip出力: [10, 20] + [11, 22] = [21, 42]
        val result = skip._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 21.0, actual = output[0])
        assertEquals(expected = 42.0, actual = output[1])
    }

    @Test
    fun `SkipD1の_train=skip pathとmain pathの勾配を足して返す`() {
        // サブ層: Bias([0, 0, 0]) - 恒等変換
        val biasWeight = IOType.d1(listOf(0.0, 0.0, 0.0))
        val biasLayer = BiasD1(
            outputSize = 3,
            optimizer = Sgd(0.1).d1(size = 3),
            weight = biasWeight,
        )

        val skip = SkipD1(
            layers = listOf(biasLayer),
            outputSize = 3,
        )

        // input = [1, 2, 3]
        val input = listOf(IOType.d1(listOf(1.0, 2.0, 3.0)))

        // 次の層からのdelta = [10, 20, 30]
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(10.0, 20.0, 30.0)))
        }

        val result = skip._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1

        // skip pathの勾配: [10, 20, 30] (deltaがそのまま流れる)
        // main pathの勾配: [10, 20, 30] (biasは勾配をそのまま返す)
        // 合計: [20, 40, 60]
        assertEquals(expected = 20.0, actual = dx[0])
        assertEquals(expected = 40.0, actual = dx[1])
        assertEquals(expected = 60.0, actual = dx[2])
    }

    @Test
    fun `SkipD1の_train=サブ層の重みが正しく更新される`() {
        // サブ層1: Affine([[1, 0], [0, 1]]) - 恒等行列
        val affineWeight = IOType.d2(2, 2) { x, y -> if (x == y) 1.0 else 0.0 }
        val affineLayer = AffineD1(
            outputSize = 2,
            optimizer = Sgd(0.1).d2(2, 2),
            weight = affineWeight,
        )

        // サブ層2: Bias([1, 1])
        val biasWeight = IOType.d1(listOf(1.0, 1.0))
        val biasLayer = BiasD1(
            outputSize = 2,
            optimizer = Sgd(0.1).d1(size = 2),
            weight = biasWeight,
        )

        val skip = SkipD1(
            layers = listOf(affineLayer, biasLayer),
            outputSize = 2,
        )

        // input = [2, 3]
        val input = listOf(IOType.d1(listOf(2.0, 3.0)))

        // 次の層からのdelta = [1, 2]
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 2.0)))
        }

        // train実行前
        // affine出力: [[1,0],[0,1]]^T dot [2, 3] = [2, 3]
        // bias出力: [2, 3] + [1, 1] = [3, 4]
        // skip出力: [2, 3] + [3, 4] = [5, 7]

        skip._train(input, calcDelta)

        // train実行後の期待値
        // bias更新: [1, 1] - 0.1 * [1, 2] = [0.9, 0.8]
        // affine更新: [[1,0],[0,1]] - 0.1 * [[2],[3]] dot [[1,2]]
        //           = [[1,0],[0,1]] - [[0.2,0.4],[0.3,0.6]]
        //           = [[0.8,-0.4],[-0.3,0.4]]

        // 更新後のexpect
        // affine出力: [[0.8,-0.3],[-0.4,0.4]] dot [2, 3] = [0.7, 0.4]
        // bias出力: [0.7, 0.4] + [0.9, 0.8] = [1.6, 1.2]
        // skip出力: [2, 3] + [1.6, 1.2] = [3.6, 4.2]
        val afterOutput = skip._expect(input)[0] as IOType.D1

        assertEquals(expected = 3.6, actual = afterOutput[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 4.2, actual = afterOutput[1], absoluteTolerance = 1e-10)
    }
}

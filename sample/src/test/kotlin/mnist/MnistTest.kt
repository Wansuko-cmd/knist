@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")

package mnist

import dataset.mnist.createMnistModel
import kotlin.test.Test

private const val EPOC = 1
private const val SEED = 0

class MnistTest {
    @Test
    fun `Mnistモデルの精度が落ちていないか確認`() {
        val actual = createMnistModel(epoc = EPOC, seed = SEED)
        assert(actual > 0.9f) { "精度が90%を割っています" }
    }
}

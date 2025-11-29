package mnist

import com.wsr.BLAS
import dataset.mnist.createMnistModel
import kotlin.test.Test
import org.junit.Before

private const val EPOC = 1
private const val SEED = 0

class MnistTest {
    @Before
    fun setup() {
        println(
            """
                設定
                BLAS is Native: ${BLAS.isNative}
            """.trimIndent(),
        )
    }

    @Test
    fun test() {
        createMnistModel(epoc = EPOC, seed = SEED)
    }
}

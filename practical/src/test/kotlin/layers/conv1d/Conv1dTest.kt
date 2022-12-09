package layers.conv1d

import com.google.common.truth.Truth.assertThat
import layers.layer1d.conv1d
import layers.layer1d.deConv1d
import kotlin.system.measureNanoTime
import kotlin.test.Test

class Conv1dTest {
    @Test
    fun conv1d() {
        val input = arrayOf(2.0, 1.0, 0.0, -1.0)
        val kernel = arrayOf(1.0, 2.0)
        val output = Array(input.size - kernel.size + 1) { 0.0 }
        input.conv1d(kernel, output) { it }
        assertThat(output).isEqualTo(arrayOf(4.0, 1.0, -2.0))
    }

    @Test
    fun deConv1d() {
        val input = arrayOf(2.0, 1.0, 0.0, -1.0)
        val kernel = arrayOf(1.0, 2.0)
        val output = Array(5) { 0.0 }
        input.deConv1d(kernel, output)
        assertThat(output).isEqualTo(arrayOf(4.0, 4.0, 1.0, -2.0, -1.0))
    }
}

package layers.conv1d

import com.google.common.truth.Truth.assertThat
import jdk.incubator.vector.DoubleVector
import layers.layer1d.conv1d
import layers.layer1d.deConv1d
import kotlin.system.measureNanoTime
import kotlin.test.Test

class Conv1dTest {
    @Test
    fun conv1d() {
        val input = doubleArrayOf(2.0, 1.0, 0.0, -1.0)
        val kernel = doubleArrayOf(1.0, 2.0)
        val output = DoubleArray(input.size - kernel.size + 1) { 0.0 }
        input.conv1d(kernel, output)
        assertThat(output).isEqualTo(doubleArrayOf(4.0, 1.0, -2.0))
        measureNanoTime { (1..100).forEach { input.conv1d(kernel, output) } }.let { println(it) }
    }

    @Test
    fun deConv1d() {
        val input = doubleArrayOf(2.0, 1.0, 0.0, -1.0)
        val kernel = doubleArrayOf(1.0, 2.0)
        val output = DoubleArray(5) { 0.0 }
        input.deConv1d(kernel, output)
        assertThat(output).isEqualTo(doubleArrayOf(4.0, 4.0, 1.0, -2.0, -1.0))
    }
}

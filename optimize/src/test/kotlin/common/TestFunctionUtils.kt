@file:Suppress("NonAsciiCharacters", "TestFunctionName")

package common

import com.google.common.truth.Truth.assertThat
import kotlin.system.measureNanoTime
import kotlin.test.Test

class TestFunctionUtils {
    @Test
    fun conv() {
        val input = listOf(
            listOf(1.0, 2.0, 3.0, 4.0, 5.0),
            listOf(6.0, 7.0, 8.0, 9.0, 10.0),
            listOf(11.0, 12.0, 13.0, 14.0, 15.0),
            listOf(16.0, 17.0, 18.0, 19.0, 20.0),
            listOf(21.0, 22.0, 23.0, 24.0, 25.0),
        )
        val kernel = listOf(
            listOf(1.0, 2.0, 3.0),
            listOf(4.0, 5.0, 6.0),
            listOf(7.0, 8.0, 9.0),
        )
        val expect = listOf(
            listOf(411.0, 456.0, 501.0),
            listOf(636.0, 681.0, 726.0),
            listOf(861.0, 906.0, 951.0),
        )
        measureNanoTime { (1..100).forEach { _ -> input.conv(kernel) } }.let { println(it) }
        assertThat(input.conv(kernel)).isEqualTo(expect)
    }
    // 10546696

    @Test
    fun convArray() {
        val input = arrayOf(
            arrayOf(1.0, 2.0, 3.0, 4.0, 5.0),
            arrayOf(6.0, 7.0, 8.0, 9.0, 10.0),
            arrayOf(11.0, 12.0, 13.0, 14.0, 15.0),
            arrayOf(16.0, 17.0, 18.0, 19.0, 20.0),
            arrayOf(21.0, 22.0, 23.0, 24.0, 25.0),
        )
        val kernel = arrayOf(
            arrayOf(1.0, 2.0, 3.0),
            arrayOf(4.0, 5.0, 6.0),
            arrayOf(7.0, 8.0, 9.0),
        )
        val expect = arrayOf(
            arrayOf(411.0, 456.0, 501.0),
            arrayOf(636.0, 681.0, 726.0),
            arrayOf(861.0, 906.0, 951.0),
        )
        measureNanoTime { (1..1000).forEach { _ -> input.convArray(kernel) } }.let { println(it) }
        assertThat(input.convArray(kernel)).isEqualTo(expect)
    }
    //4890900

    @Test
    fun addArray() {
        val input = Array(5) { Array(5) { 5.0 } }
        val other = Array(5) { Array(5) { 3.0 } }
        val expect = Array(5) { Array(5) { 8.0 } }

        val clone = Array(input.size) { input[it].clone() }
        measureNanoTime {
            (1..100)
                .forEach { _ -> clone.add(other) }
        }.let { println(it) }
        input.add(other)
        assertThat(input).isEqualTo(expect)
    }
    // 2725500
}

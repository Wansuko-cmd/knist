package com.wsr.knist.network

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unwrap
import com.wsr.knist.core.IOType
import com.wsr.knist.core.unwrap
import kotlin.math.abs
import kotlin.test.fail

fun <T : IOType> assertContentEquals(expected: T, actual: T, absoluteTolerance: Float = 1e-4f) {
    if (expected.shape != actual.shape) {
        fail("Expected <$expected> with absolute tolerance <$absoluteTolerance>, actual <$actual>.")
    }
    val expectedValues = expected.flat()
    val actualValues = actual.flat()
    repeat(expected.size) {
        if (abs(expectedValues[it] - actualValues[it]) > absoluteTolerance) {
            fail("Expected <$expected> with absolute tolerance <$absoluteTolerance>, actual <$actual>.")
        }
    }
}

fun <T : IOType> assertContentEquals(expected: Batch<T>, actual: Batch<T>, absoluteTolerance: Float = 1e-4f) {
    if (expected.shape != actual.shape) {
        fail("Expected <$expected> with absolute tolerance <$absoluteTolerance>, actual <$actual>.")
    }
    val expectedValues = expected.unwrap()
    val actualValues = actual.unwrap()
    repeat(expected.size) {
        if (abs(expectedValues[it] - actualValues[it]) > absoluteTolerance) {
            fail("Expected <$expected> with absolute tolerance <$absoluteTolerance>, actual <$actual>.")
        }
    }
}

fun <T : IOType> assertContentEquals(expected: List<T>, actual: List<T>, absoluteTolerance: Float = 1e-4f) {
    if (expected.size != actual.size) {
        fail("Expected <$expected> with absolute tolerance <$absoluteTolerance>, actual <$actual>.")
    }
    repeat(expected.size) {
        assertContentEquals(expected[it], actual[it])
    }
}

private fun IOType.flat(): FloatArray = when (this) {
    is IOType.D0 -> floatArrayOf(unwrap())
    is IOType.D1 -> unwrap()
    is IOType.D2 -> unwrap()
    is IOType.D3 -> unwrap()
    is IOType.D4 -> unwrap()
}

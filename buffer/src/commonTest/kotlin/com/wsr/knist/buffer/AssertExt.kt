package com.wsr.knist.buffer

import com.wsr.knist.base.data.DataBuffer
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.fail

fun assertContentEquals(expected: DataBuffer, actual: DataBuffer, absoluteTolerance: Float = 1e-4f, relativeTolerance: Float = 1e-4f) {
    if (expected.size != actual.size) {
        fail("Expected <$expected> with absolute tolerance <$relativeTolerance>, actual <$actual>.")
    }
    repeat(expected.size) {
        val e = expected[it]
        val a = actual[it]
        val tolerance = max(absoluteTolerance, relativeTolerance * max(abs(e), abs(a)))
        if (abs(e - a) > tolerance) {
            fail("Expected <$expected> with absolute tolerance <$relativeTolerance>, actual <$actual>.")
        }
    }
}

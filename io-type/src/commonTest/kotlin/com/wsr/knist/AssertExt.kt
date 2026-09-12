package com.wsr.knist

import com.wsr.knist.core.IOType
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.fail

fun assertContentEquals(expected: IOType, actual: IOType, absoluteTolerance: Float = 1e-4f, relativeTolerance: Float = 1e-4f) {
    if (expected.shape != actual.shape) {
        fail("Expected <$expected> with absolute tolerance <$relativeTolerance>, actual <$actual>.")
    }
    repeat(expected.size) {
        val e = expected.value[it]
        val a = actual.value[it]
        val tolerance = max(absoluteTolerance, relativeTolerance * max(abs(e), abs(a)))
        if (abs(e - a) > tolerance) {
            fail("Expected <$expected> with absolute tolerance <$relativeTolerance>, actual <$actual>.")
        }
    }
}

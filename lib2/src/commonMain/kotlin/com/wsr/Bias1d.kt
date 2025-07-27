package com.wsr

import com.wsr.common.IOType1d
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus

class Bias1d(numOfInput: Int) : Layer2<IOType1d> {
    private val weight = Array(numOfInput) { 0.0 }
    override fun expect(input: IOType1d): IOType1d {
        for (i in input.indices) {
            input[i] += weight[i]
        }
        return input
    }

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        for (i in input.indices) {
            input[i] += weight[i]
        }
        val delta = delta(input)
        for (i in weight.indices) {
            weight[i] -= delta[i]
        }
        return delta
    }
}

package com.wsr

import com.wsr.common.IOType1d
import kotlin.math.exp

class Sigmoid1d : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = input

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val output = Array(input.size) { 1 / (1 + exp(-input[it])) }
        val delta = delta(output)
        return Array(delta.size) { delta[it] * (1 - delta[it]) }
    }
}
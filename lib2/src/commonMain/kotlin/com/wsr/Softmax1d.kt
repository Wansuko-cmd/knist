package com.wsr

import com.wsr.common.IOType1d
import kotlin.math.exp

class Softmax1d : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = input

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val max = input.max()
        val exp = input.map { exp(it - max) }
        val sum = exp.sum()
        val output = Array(input.size) { exp[it] / sum }
        return delta(output)
    }
}

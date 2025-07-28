package com.wsr

import com.wsr.common.IOType1d

class Relu1d : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d
    ): IOType1d {
        val output = forward(input)
        val delta = delta(output)
        for (i in output.indices) {
            if (output[i] <= 0) delta[i] = 0.0
        }
        return delta
    }

    private fun forward(input: IOType1d): IOType1d {
        val output = Array(input.size) { 0.0 }
        for (i in input.indices) {
            output[i] = input[i].coerceAtLeast(0.0)
        }
        return output
    }
}
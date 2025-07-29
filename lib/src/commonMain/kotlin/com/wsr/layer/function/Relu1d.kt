package com.wsr.layer.function

import com.wsr.layer.Layer
import com.wsr.common.IOType1d

class Relu1d : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d
    ): IOType1d {
        val output = forward(input)
        val delta = delta(output)
        return Array(delta.size) { if (output[it] <= 0.0) 0.0 else delta[it] }
    }

    private fun forward(input: IOType1d): IOType1d {
        return Array(input.size) { input[it].coerceAtLeast(0.0) }
    }
}
package com.wsr.layers

import com.wsr.common.IOTypeD1

interface Layer {
    val numOfInput: Int
    val numOfOutput: Int
    fun expect(input: IOTypeD1): IOTypeD1
    fun train(input: IOTypeD1, delta: (output: IOTypeD1) -> IOTypeD1): IOTypeD1
}

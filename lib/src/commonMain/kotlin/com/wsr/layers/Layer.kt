package com.wsr.layers

import com.wsr.common.IOTypeD1
import kotlinx.serialization.Serializable

@Serializable
abstract class Layer {
    abstract val numOfInput: Int
    abstract val numOfOutput: Int
    abstract fun expect(input: IOTypeD1): IOTypeD1
    abstract fun train(input: IOTypeD1, delta: (output: IOTypeD1) -> IOTypeD1): IOTypeD1
}

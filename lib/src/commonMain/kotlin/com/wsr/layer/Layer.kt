package com.wsr.layer

interface Layer<IOType> {
    val numOfInput: Int
    val numOfOutput: Int
    fun expect(input: IOType): IOType
    fun train(input: IOType, delta: (output: IOType) -> IOType): IOType
}
package com.wsr.layer

interface Layer<IOType> {
    fun expect(input: IOType): IOType
    fun train(input: IOType, delta: (output: IOType) -> IOType): IOType
}
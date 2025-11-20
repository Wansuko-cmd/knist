package com.wsr.layer

import com.wsr.IOType

interface Layer {
    @Suppress("FunctionName")
    fun _expect(input: List<IOType>, context: Context): List<IOType>

    @Suppress("FunctionName")
    fun _train(input: List<IOType>, context: Context, calcDelta: (List<IOType>) -> List<IOType>): List<IOType>
}

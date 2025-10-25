package com.wsr.layer

import com.wsr.IOType

interface Layer {
    @Suppress("FunctionName")
    fun _expect(input: List<IOType>): List<IOType>

    @Suppress("FunctionName")
    fun _train(input: List<IOType>, calcDelta: (List<IOType>) -> List<IOType>): List<IOType>
}

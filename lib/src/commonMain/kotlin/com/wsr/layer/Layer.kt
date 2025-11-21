package com.wsr.layer

import com.wsr.Batch
import com.wsr.IOType

interface Layer {
    @Suppress("FunctionName")
    fun _expect(input: Batch<IOType>, context: Context): Batch<IOType>

    @Suppress("FunctionName")
    fun _train(input: Batch<IOType>, context: Context, calcDelta: (Batch<IOType>) -> Batch<IOType>): Batch<IOType>
}

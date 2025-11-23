package com.wsr.layer

import com.wsr.batch.Batch
import com.wsr.core.IOType

interface Layer {
    @Suppress("FunctionName")
    fun _expect(input: Batch<IOType>, context: Context): Batch<IOType>

    @Suppress("FunctionName")
    fun _train(input: Batch<IOType>, context: Context, calcDelta: (Batch<IOType>) -> Batch<IOType>): Batch<IOType>
}

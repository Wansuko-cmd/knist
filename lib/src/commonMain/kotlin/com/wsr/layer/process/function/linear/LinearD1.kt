package com.wsr.layer.process.function.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LinearD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, context: Context, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> =
        calcDelta(input)
}

fun <T> NetworkBuilder.D1<T>.linear() = addProcess(LinearD1(outputSize = inputSize))

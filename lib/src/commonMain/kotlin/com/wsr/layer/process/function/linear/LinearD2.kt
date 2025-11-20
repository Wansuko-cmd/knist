package com.wsr.layer.process.function.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LinearD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: List<IOType.D2>, context: Context): List<IOType.D2> = input

    override fun train(
        input: List<IOType.D2>,
        context: Context,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> = calcDelta(input)
}

fun <T> NetworkBuilder.D2<T>.linear() = addProcess(
    process = LinearD2(outputX = inputX, outputY = inputY),
)

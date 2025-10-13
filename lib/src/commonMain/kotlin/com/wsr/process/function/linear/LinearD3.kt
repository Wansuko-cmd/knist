package com.wsr.process.function.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LinearD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> =
        calcDelta(input)
}

fun <T : IOType> NetworkBuilder.D3<T>.linear() = addProcess(
    process = LinearD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)

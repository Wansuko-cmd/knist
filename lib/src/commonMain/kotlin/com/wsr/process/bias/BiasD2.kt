package com.wsr.process.bias

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.average.average
import com.wsr.d2.minus
import com.wsr.d2.times
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class BiasD2(
    override val outputX: Int,
    override val outputY: Int,
    private val rate: Double,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input + weight

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input + weight
        val delta = calcDelta(output)
        weight -= rate * delta.average()
        return delta
    }
}

fun <T: IOType> NetworkBuilder.D2<T>.bias() = addProcess(
    process = BiasD2(
        outputX = inputX,
        outputY = inputY,
        rate = rate,
        weight = IOType.d2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) }
    )
)

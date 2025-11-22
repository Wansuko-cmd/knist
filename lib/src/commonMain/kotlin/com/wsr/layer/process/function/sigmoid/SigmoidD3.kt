package com.wsr.layer.process.function.sigmoid

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SigmoidD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.map(::forward)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }

    private fun forward(input: IOType.D3): IOType.D3 = IOType.d3(
        i = outputX,
        j = outputY,
        k = outputZ,
    ) { x, y, z -> 1 / (1 + exp(-input[x, y, z])) }
}

fun <T> NetworkBuilder.D3<T>.sigmoid() = addProcess(
    process = SigmoidD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)

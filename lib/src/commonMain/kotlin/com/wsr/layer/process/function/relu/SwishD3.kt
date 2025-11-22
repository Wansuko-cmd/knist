package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.plus.plus
import com.wsr.batch.times.times
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SwishD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.map { input ->
        IOType.d3(
            i = outputX,
            j = outputY,
            k = outputZ,
        ) { x, y, z -> input[x, y, z] / (1 + exp(-input[x, y, z])) }
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val sigmoid = input.map { input ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z -> 1 / (1 + exp(-input[x, y, z])) }
        }
        val output = input * sigmoid
        val delta = calcDelta(output)
        return (output + sigmoid * (1f - output)) * delta
    }
}

fun <T> NetworkBuilder.D3<T>.swish() = addProcess(
    process = SwishD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)

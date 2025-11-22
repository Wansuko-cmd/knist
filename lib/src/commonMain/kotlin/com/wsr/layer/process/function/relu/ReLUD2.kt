package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapValue
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class ReLUD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        return Batch(input.size) { i ->
            IOType.d2(
                outputX,
                outputY,
            ) { x, y -> if (input[i][x, y] >= 0f) delta[i][x, y] else 0f }
        }
    }

    private fun forward(input: Batch<IOType.D2>): Batch<IOType.D2> = input.mapValue { if (it >= 0f) it else 0f }
}

fun <T> NetworkBuilder.D2<T>.reLU() = addProcess(ReLUD2(outputX = inputX, outputY = inputY))

package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.toList().map(::forward).toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val output = input.map(::forward)
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                if (input[i][x, y] >= 0f) delta[i][x, y] else 0.01f * delta[i][x, y]
            }
        }.toBatch()
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.d2(outputX, outputY) { x, y ->
        if (input[x, y] >=
            0f
        ) {
            input[x, y]
        } else {
            0.01f
        }
    }
}

fun <T> NetworkBuilder.D2<T>.leakyReLU() = addProcess(LeakyReLUD2(outputX = inputX, outputY = inputY))

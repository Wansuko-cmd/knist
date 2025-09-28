package com.wsr.layers.dropout

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.d2.times
import com.wsr.layers.Process
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val ratio: Double,
    private val seed: Int? = null,
) : Process.D2() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = ratio * input

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val mask = IOType.d2(outputX, outputY) { _, _ ->
            if (random.nextDouble(0.0, 1.0) <= ratio) 1.0 else 0.0
        }
        val output = input.map { input ->
            IOType.d2(outputX, outputY) { x, y -> input[x, y] * mask[x, y] }
        }
        val delta = calcDelta(output)
        return delta.map { delta ->
            IOType.d2(outputX, outputY) { x, y -> delta[x, y] * mask[x, y] }
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.dropout(ratio: Double, seed: Int? = null) = addProcess(
    layer = DropoutD2(
        outputX = inputX,
        outputY = inputY,
        ratio = ratio,
        seed = seed,
    ),
)

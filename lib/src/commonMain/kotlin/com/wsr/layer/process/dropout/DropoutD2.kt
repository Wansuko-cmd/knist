package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.process.Process
import com.wsr.operator.times
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class DropoutD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val ratio: Double,
    private val seed: Int? = null,
) : Process.D2() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val mask = IOType.d2(outputX, outputY) { _, _ ->
            if (random.nextDouble(0.0, 1.0) <= ratio) q else 0.0
        }
        val output = input * mask
        val delta = calcDelta(output)
        return delta * mask
    }
}

fun <T> NetworkBuilder.D2<T>.dropout(ratio: Double, seed: Int? = null) = addProcess(
    process =
    DropoutD2(
        outputX = inputX,
        outputY = inputY,
        ratio = ratio,
        seed = seed,
    ),
)

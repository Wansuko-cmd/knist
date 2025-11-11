package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.process.Process
import com.wsr.operator.times
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val ratio: Double,
    private val seed: Int? = null,
) : Process.D3() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val mask = IOType.d3(
            i = outputX,
            j = outputY,
            k = outputZ,
        ) { _, _, _ -> if (random.nextDouble(0.0, 1.0) <= ratio) q else 0.0 }
        val output = input * mask
        val delta = calcDelta(output)
        return delta * mask
    }
}

fun <T> NetworkBuilder.D3<T>.dropout(ratio: Double, seed: Int? = null) = addProcess(
    process =
    DropoutD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        ratio = ratio,
        seed = seed,
    ),
)

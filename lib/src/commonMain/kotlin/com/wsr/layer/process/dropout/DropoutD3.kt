package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.nextFloat
import com.wsr.operator.times
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val ratio: Float,
    private val seed: Int? = null,
) : Process.D3() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun expect(input: List<IOType.D3>, context: Context): List<IOType.D3> = input

    override fun train(input: List<IOType.D3>, context: Context, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val mask = IOType.d3(
            i = outputX,
            j = outputY,
            k = outputZ,
        ) { _, _, _ -> if (random.nextFloat(0f, 1f) <= ratio) q else 0f }
        val output = input * mask
        val delta = calcDelta(output)
        return delta * mask
    }
}

fun <T> NetworkBuilder.D3<T>.dropout(ratio: Float, seed: Int? = null) = addProcess(
    process =
    DropoutD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        ratio = ratio,
        seed = seed,
    ),
)

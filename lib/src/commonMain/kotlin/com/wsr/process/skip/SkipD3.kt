@file:Suppress("UNCHECKED_CAST")

package com.wsr.process.skip

import com.wsr.IOType
import com.wsr.Layer
import com.wsr.NetworkBuilder
import com.wsr.operator.plus
import com.wsr.process.Process
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D3 = (List<IOType.D3>) -> List<IOType.D3>

@Serializable
class SkipD3 internal constructor(
    // List<Process.D3>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> =
        input + layers.fold(input) { acc, layer -> layer._expect(acc) as List<IOType.D3> }

    private val trainChain: ((List<IOType.D3>) -> List<IOType.D3>) -> CALC_DELTA_D3 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D3 -> final },
        ) { layer, acc ->
            { final ->
                { input ->
                    layer._train(input) { acc(final)(it as List<IOType.D3>) } as List<IOType.D3>
                }
            }
        }
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        var skipDelta: List<IOType.D3> = emptyList()

        val final: CALC_DELTA_D3 = { acc ->
            val output = input + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input)

        return mainDelta + skipDelta
    }
}

fun <T : IOType> NetworkBuilder.D3<T>.skip(
    builder: NetworkBuilder.D3<T>.() -> NetworkBuilder.D3<T>,
): NetworkBuilder.D3<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D3>()

    val last = layers.last()
    check(inputX == last.outputX && inputY == last.outputY && inputZ == last.outputZ) {
        """
            invalid layers.
            inputX: $inputX
            inputY: $inputY
            inputZ: $inputZ
            outputX: ${last.outputX}
            outputY: ${last.outputY}
            outputZ: ${last.outputZ}
        """.trimIndent()
    }

    return addProcess(
        process = SkipD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            layers = layers,
        ),
    )
}

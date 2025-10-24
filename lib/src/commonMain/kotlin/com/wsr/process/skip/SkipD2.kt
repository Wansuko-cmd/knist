@file:Suppress("UNCHECKED_CAST")

package com.wsr.process.skip

import com.wsr.IOType
import com.wsr.Layer
import com.wsr.NetworkBuilder
import com.wsr.operator.plus
import com.wsr.process.Process
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D2 = (List<IOType.D2>) -> List<IOType.D2>

@Serializable
class SkipD2 internal constructor(
    // List<Process.D2>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        return input + layers.fold(input) { acc, layer -> layer._expect(acc) as List<IOType.D2> }
    }

    private val trainChain: ((List<IOType.D2>) -> List<IOType.D2>) -> CALC_DELTA_D2 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D2 -> final },
        ) { layer, acc ->
            { final ->
                { input ->
                    layer._train(input) { acc(final)(it as List<IOType.D2>) } as List<IOType.D2>
                }
            }
        }
    }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        var skipDelta: List<IOType.D2> = emptyList()

        val final: CALC_DELTA_D2 = { acc ->
            val output = input + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input)

        return mainDelta + skipDelta
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.skip(
    builder: NetworkBuilder.D2<T>.() -> NetworkBuilder.D2<T>,
): NetworkBuilder.D2<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D2>()

    val last = layers.last()
    check(inputX == last.outputX && inputY == last.outputY) {
        """
            invalid layers.
            inputX: $inputX
            inputY: $inputY
            outputX: ${last.outputX}
            outputY: ${last.outputY}
        """.trimIndent()
    }

    return addProcess(
        process = SkipD2(
            outputX = inputX,
            outputY = inputY,
            layers = layers,
        ),
    )
}

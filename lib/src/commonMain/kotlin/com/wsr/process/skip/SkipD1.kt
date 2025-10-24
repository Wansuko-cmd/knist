@file:Suppress("UNCHECKED_CAST")

package com.wsr.process.skip

import com.wsr.IOType
import com.wsr.Layer
import com.wsr.NetworkBuilder
import com.wsr.operator.plus
import com.wsr.process.Process
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D1 = (List<IOType.D1>) -> List<IOType.D1>

@Serializable
class SkipD1 internal constructor(
    // List<Process.D1>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    override val outputSize: Int,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        return input + layers.fold(input) { acc, layer -> layer._expect(acc) as List<IOType.D1> }
    }

    private val trainChain: ((List<IOType.D1>) -> List<IOType.D1>) -> CALC_DELTA_D1 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D1 -> final },
        ) { layer, acc ->
            { final ->
                { input ->
                    layer._train(input) { acc(final)(it as List<IOType.D1>) } as List<IOType.D1>
                }
            }
        }
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        var skipDelta: List<IOType.D1> = emptyList()

        val final: CALC_DELTA_D1 = { acc ->
            val output = input + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input)

        return mainDelta + skipDelta
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.skip(
    builder: NetworkBuilder.D1<T>.() -> NetworkBuilder.D1<T>,
): NetworkBuilder.D1<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D1>()

    check(inputSize == layers.last().outputSize) {
        """
            invalid layers.
            inputSize: $inputSize
            outputSize: ${layers.last().outputSize}
        """.trimIndent()
    }

    return addProcess(
        process = SkipD1(
            outputSize = inputSize,
            layers = layers,
        ),
    )
}

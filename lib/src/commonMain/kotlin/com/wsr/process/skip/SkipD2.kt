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
    private val inputX: Int,
    private val inputY: Int,
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    private val resizeToOutput: (IOType.D2) -> IOType.D2 by lazy {
        when {
            inputX == outputX && inputY == outputY -> { it: IOType.D2 -> it }
            inputX <= outputX && inputY <= outputY -> { it: IOType.D2 ->
                val result = IOType.d2(listOf(outputX, outputY))
                for (i in 0 until inputX) {
                    for (j in 0 until inputY) {
                        result[i, j] = it[i, j]
                    }
                }
                result
            }

            inputX % outputX == 0 && inputY % outputY == 0 -> { it: IOType.D2 ->
                val strideX = inputX / outputX
                val strideY = inputY / outputY
                IOType.d2(outputX, outputY) { x, y ->
                    val startX = x * strideX
                    val startY = y * strideY
                    var sum = 0.0
                    for (dx in 0 until strideX) {
                        for (dy in 0 until strideY) {
                            sum += it[startX + dx, startY + dy]
                        }
                    }
                    sum / (strideX * strideY)
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    private val resizeToInput: (IOType.D2) -> IOType.D2 by lazy {
        when {
            inputX == outputX && inputY == outputY -> { it: IOType.D2 -> it }
            inputX <= outputX && inputY <= outputY -> { it: IOType.D2 ->
                IOType.d2(inputX, inputY) { x, y -> it[x, y] }
            }

            inputX % outputX == 0 && inputY % outputY == 0 -> { it: IOType.D2 ->
                val strideX = inputX / outputX
                val strideY = inputY / outputY
                IOType.d2(inputX, inputY) { x, y ->
                    it[x / strideX, y / strideY] / (strideX * strideY)
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val main = layers.fold(input) { acc, layer -> layer._expect(acc) as List<IOType.D2> }
        val skip = input.map(resizeToOutput)
        return main + skip
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

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        var skipDelta: List<IOType.D2> = emptyList()

        val final: CALC_DELTA_D2 = { acc ->
            val output = input.map(resizeToOutput) + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input)

        return mainDelta + skipDelta.map(resizeToInput)
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.skip(
    builder: NetworkBuilder.D2<T>.() -> NetworkBuilder.D2<T>,
): NetworkBuilder.D2<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D2>()
    val last = layers.last()

    check(
        (inputX == last.outputX && inputY == last.outputY) ||
            (inputX < last.outputX && inputY < last.outputY) ||
            (inputX % last.outputX == 0 && inputY % last.outputY == 0),
    ) {
        """
            invalid parameter.
            input: ($inputX, $inputY)
            output: (${last.outputX}, ${last.outputY})
        """.trimIndent()
    }

    return addProcess(
        process = SkipD2(
            inputX = inputX,
            inputY = inputY,
            outputX = last.outputX,
            outputY = last.outputY,
            layers = layers,
        ),
    )
}

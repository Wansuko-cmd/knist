@file:Suppress("UNCHECKED_CAST")

package com.wsr.layer.process.skip

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.Layer
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D3 = (input: List<IOType.D3>, context: Context) -> List<IOType.D3>

@Serializable
class SkipD3 internal constructor(
    // List<Process.D3>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    private val inputX: Int,
    private val inputY: Int,
    private val inputZ: Int,
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : Process.D3() {
    private val resizeToOutput: (IOType.D3) -> IOType.D3 by lazy {
        when {
            inputX == outputX && inputY == outputY && inputZ == outputZ -> { it: IOType.D3 -> it }
            inputX <= outputX && inputY <= outputY && inputZ <= outputZ -> { it: IOType.D3 ->
                val result = IOType.d3(listOf(outputX, outputY, outputZ))
                for (i in 0 until inputX) {
                    for (j in 0 until inputY) {
                        for (k in 0 until inputZ) {
                            result[i, j, k] = it[i, j, k]
                        }
                    }
                }
                result
            }

            inputX % outputX == 0 && inputY % outputY == 0 && inputZ % outputZ == 0 -> { it: IOType.D3 ->
                val strideX = inputX / outputX
                val strideY = inputY / outputY
                val strideZ = inputZ / outputZ
                IOType.d3(outputX, outputY, outputZ) { x, y, z ->
                    val startX = x * strideX
                    val startY = y * strideY
                    val startZ = z * strideZ
                    var sum = 0f
                    for (dx in 0 until strideX) {
                        for (dy in 0 until strideY) {
                            for (dz in 0 until strideZ) {
                                sum += it[startX + dx, startY + dy, startZ + dz]
                            }
                        }
                    }
                    sum / (strideX * strideY * strideZ)
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    private val resizeToInput: (IOType.D3) -> IOType.D3 by lazy {
        when {
            inputX == outputX && inputY == outputY && inputZ == outputZ -> { it: IOType.D3 -> it }
            inputX <= outputX && inputY <= outputY && inputZ <= outputZ -> { it: IOType.D3 ->
                IOType.d3(inputX, inputY, inputZ) { x, y, z -> it[x, y, z] }
            }

            inputX % outputX == 0 && inputY % outputY == 0 && inputZ % outputZ == 0 -> { it: IOType.D3 ->
                val strideX = inputX / outputX
                val strideY = inputY / outputY
                val strideZ = inputZ / outputZ
                IOType.d3(inputX, inputY, inputZ) { x, y, z ->
                    it[x / strideX, y / strideY, z / strideZ] / (strideX * strideY * strideZ)
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    override fun expect(input: List<IOType.D3>, context: Context): List<IOType.D3> {
        val main = layers.fold(input) { acc, layer -> layer._expect(acc, context) as List<IOType.D3> }
        val skip = input.map(resizeToOutput)
        return main + skip
    }

    private val trainChain: (CALC_DELTA_D3) -> CALC_DELTA_D3 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D3 -> final },
        ) { layer, acc ->
            { final ->
                { input, context ->
                    layer._train(input, context) { acc(final)(it as List<IOType.D3>, context) } as List<IOType.D3>
                }
            }
        }
    }

    override fun train(
        input: List<IOType.D3>,
        context: Context,
        calcDelta: (List<IOType.D3>) -> List<IOType.D3>,
    ): List<IOType.D3> {
        var skipDelta: List<IOType.D3> = emptyList()

        val final: CALC_DELTA_D3 = { acc, context ->
            val output = input.map(resizeToOutput) + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input, context)

        return mainDelta + skipDelta.map(resizeToInput)
    }
}

fun <T> NetworkBuilder.D3<T>.skip(builder: NetworkBuilder.D3<T>.() -> NetworkBuilder.D3<T>): NetworkBuilder.D3<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D3>()
    val last = layers.last()

    check(
        (inputX == last.outputX && inputY == last.outputY && inputZ == last.outputZ) ||
            (inputX < last.outputX && inputY < last.outputY && inputZ < last.outputZ) ||
            (inputX % last.outputX == 0 && inputY % last.outputY == 0 && inputZ % last.outputZ == 0),
    ) {
        """
            invalid parameter.
            input: ($inputX, $inputY, $inputZ)
            output: (${last.outputX}, ${last.outputY}. ${last.outputZ})
        """.trimIndent()
    }

    return addProcess(
        process = SkipD3(
            inputX = inputX,
            inputY = inputY,
            inputZ = inputZ,
            outputX = last.outputX,
            outputY = last.outputY,
            outputZ = last.outputZ,
            layers = layers,
        ),
    )
}

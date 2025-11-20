@file:Suppress("UNCHECKED_CAST")

package com.wsr.layer.process.skip

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.Layer
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D1 = (input: List<IOType.D1>, context: Context) -> List<IOType.D1>

@Serializable
class SkipD1 internal constructor(
    // List<Process.D1>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    private val inputSize: Int,
    override val outputSize: Int,
) : Process.D1() {
    private val resizeToOutput: (IOType.D1) -> IOType.D1 by lazy {
        when {
            inputSize == outputSize -> { it: IOType.D1 -> it }
            inputSize < outputSize -> { it: IOType.D1 -> IOType.d1(it.value.copyOf(outputSize)) }
            inputSize % outputSize == 0 -> { it: IOType.D1 ->
                val stride = inputSize / outputSize
                val result = FloatArray(outputSize) { i ->
                    val index = i * stride
                    it.value.sliceArray(index until index + stride).average().toFloat()
                }
                IOType.d1(result)
            }
            else -> throw IllegalArgumentException()
        }
    }

    private val resizeToInput: (IOType.D1) -> IOType.D1 by lazy {
        when {
            inputSize == outputSize -> { it: IOType.D1 -> it }
            inputSize < outputSize -> { it: IOType.D1 -> IOType.d1(it.value.copyOf(inputSize)) }
            inputSize % outputSize == 0 -> { it: IOType.D1 ->
                val stride = inputSize / outputSize
                val result = FloatArray(inputSize) { i -> it.value[i / stride] / stride }
                IOType.d1(result)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D1> {
        val main = layers.fold(input) { acc, layer -> layer._expect(acc, context) as List<IOType.D1> }
        val skip = input.map(resizeToOutput)
        return main + skip
    }

    private val trainChain: (CALC_DELTA_D1) -> CALC_DELTA_D1 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D1 -> final },
        ) { layer, acc ->
            { final ->
                { input, context ->
                    layer._train(input, context) { acc(final)(it as List<IOType.D1>, context) } as List<IOType.D1>
                }
            }
        }
    }

    override fun train(
        input: List<IOType.D1>,
        context: Context,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        var skipDelta: List<IOType.D1> = emptyList()

        val final: CALC_DELTA_D1 = { acc, context ->
            val output = input.map(resizeToOutput) + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input, context)

        return mainDelta + skipDelta.map(resizeToInput)
    }
}

fun <T> NetworkBuilder.D1<T>.skip(builder: NetworkBuilder.D1<T>.() -> NetworkBuilder.D1<T>): NetworkBuilder.D1<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Process.D1>()
    val last = layers.last()

    check(
        inputSize == last.outputSize ||
            inputSize < last.outputSize ||
            inputSize % last.outputSize == 0,
    ) {
        """
            invalid parameter.
            input: ($inputSize)
            output: (${last.outputSize})
        """.trimIndent()
    }

    return addProcess(
        process = SkipD1(
            inputSize = inputSize,
            outputSize = last.outputSize,
            layers = layers,
        ),
    )
}

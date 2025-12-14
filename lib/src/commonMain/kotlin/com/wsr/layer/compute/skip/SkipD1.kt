@file:Suppress("UNCHECKED_CAST")

package com.wsr.layer.compute.skip

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.operation.plus.plus
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.reshape.slice.slice
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.layer.Layer
import com.wsr.layer.compute.Compute
import kotlinx.serialization.Serializable

private typealias CALC_DELTA_D1 = (input: Batch<IOType.D1>, context: Context) -> Batch<IOType.D1>

@Serializable
class SkipD1 internal constructor(
    // List<Process.D1>だがSerializer対策
    private val layers: List<Layer> = emptyList(),
    private val inputSize: Int,
    override val outputSize: Int,
) : Compute.D1() {
    private val resizeToOutput: (IOType.D1) -> IOType.D1 by lazy {
        when {
            inputSize == outputSize -> { it: IOType.D1 -> it }
            inputSize < outputSize -> { it: IOType.D1 ->
                val result = IOType.d1(outputSize)
                for (i in 0 until inputSize) result[i] = it[i]
                result
            }

            inputSize % outputSize == 0 -> { it: IOType.D1 ->
                val stride = inputSize / outputSize

                IOType.d1(outputSize) { i ->
                    val index = i * stride
                    it.slice(index until index + stride).average()
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    private val resizeToInput: (IOType.D1) -> IOType.D1 by lazy {
        when {
            inputSize == outputSize -> { it: IOType.D1 -> it }
            inputSize < outputSize -> { it: IOType.D1 ->
                val result = IOType.d1(inputSize)
                for (i in 0 until inputSize) result[i] = it[i]
                result
            }

            inputSize % outputSize == 0 -> { output: IOType.D1 ->
                val stride = inputSize / outputSize
                IOType.d1(inputSize) { i -> output[i / stride] / stride }
            }

            else -> throw IllegalArgumentException()
        }
    }

    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> {
        val main = layers.fold(input) { acc, layer -> layer._expect(acc, context) as Batch<IOType.D1> }
        val skip = input.map(resizeToOutput)
        return main + skip
    }

    private val trainChain: (CALC_DELTA_D1) -> CALC_DELTA_D1 by lazy {
        layers.foldRight(
            initial = { final: CALC_DELTA_D1 -> final },
        ) { layer, acc ->
            { final ->
                { input, context ->
                    layer._train(input, context) { acc(final)(it as Batch<IOType.D1>, context) } as Batch<IOType.D1>
                }
            }
        }
    }

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        var skipDelta: Batch<IOType.D1>? = null

        val final: CALC_DELTA_D1 = { acc, context ->
            val output = input.map(resizeToOutput) + acc
            calcDelta(output).also { skipDelta = it }
        }
        val mainDelta = trainChain(final)(input, context)

        return mainDelta + skipDelta!!.map(resizeToInput)
    }
}

fun <T> NetworkBuilder.D1<T>.skip(builder: NetworkBuilder.D1<T>.() -> NetworkBuilder.D1<T>): NetworkBuilder.D1<T> {
    val layers = builder().layers
        .drop(layers.size)
        .filterIsInstance<Compute.D1>()
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

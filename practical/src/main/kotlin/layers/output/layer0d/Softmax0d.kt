@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.output.layer0d

import common.iotype.IOType
import common.iotype.IOType0d
import exception.DomainException
import layers.Layer
import kotlin.math.exp
import kotlin.random.Random

data class Softmax0d(
    private val numOfNeuron: Int,
    private val type: (numOfNeuron: Int, activationFunction: (Double) -> Double) -> Layer<IOType0d>,
) : Output0dLayer {
    override fun toLayer() =
        listOf(
            type(numOfNeuron) { it },
            object : Layer<IOType0d> {
                override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }

                override inline fun forward(
                    input: IOType,
                    output: IOType,
                    weight: Array<IOType>,
                ) {
                    val inputArray = input.asIOType0d().value
                    val outputArray = output.asIOType0d().value
                    val max = inputArray.max()
                    val exp = inputArray.map { exp(it - max) }
                    val sum = exp.sum()
                    for (inputIndex in inputArray.indices) {
                        inputArray[inputIndex] = exp[inputIndex] / sum
                        outputArray[inputIndex] = inputArray[inputIndex]
                    }
                }

                override inline fun calcDelta(
                    beforeDelta: IOType,
                    beforeOutput: IOType,
                    delta: IOType,
                    weight: Array<IOType>,
                ) {
                    val beforeDeltaArray = beforeDelta.asIOType0d().value
                    val outputArray = beforeOutput.asIOType0d().value
                    val deltaArray = delta.asIOType0d().value
                    for (i in beforeDeltaArray.indices) {
                        val q = if (deltaArray[i] > 0.5) 1.0 else 0.0
                        val y = outputArray[i]
                        beforeDeltaArray[i] = y - q
                    }
                }

                override inline fun backward(
                    weight: Array<IOType>,
                    delta: IOType,
                    input: IOType,
                    rate: Double,
                ) = Unit

                override fun createWeight(input: IOType, random: Random): Array<IOType> =
                    Array(input.asIOType0d().value.size) {
                        IOType0d(DoubleArray(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
                    }

                override fun createOutput(input: IOType): IOType0d =
                    IOType0d(DoubleArray(numOfNeuron) { 0.0 })

                override fun createDelta(input: IOType): IOType0d = IOType0d(DoubleArray(numOfNeuron) { 0.0 })
            },
        )
}

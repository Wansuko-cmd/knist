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
                    val inputArray = input.asIOType0d()
                    val outputArray = output.asIOType0d()
                    val max = inputArray.inner.max()
                    val exp = inputArray.inner.map { exp(it - max) }
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
                    val beforeDeltaArray = beforeDelta.asIOType0d()
                    val outputArray = beforeOutput.asIOType0d()
                    val deltaArray = delta.asIOType0d()
                    beforeDeltaArray.inner.fill(0.0)
                    for (i in beforeDeltaArray.indices) {
                        val y = outputArray[i]
                        beforeDeltaArray[i] = y - deltaArray[i]
                    }
                }

                override inline fun backward(
                    weight: Array<IOType>,
                    delta: IOType,
                    input: IOType,
                    rate: Double,
                ) = Unit

                override fun createWeight(input: IOType, random: Random): Array<IOType> =
                    Array(input.asIOType0d().size) {
                        IOType0d(MutableList(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
                    }

                override fun createOutput(input: IOType): IOType0d =
                    IOType0d(MutableList(numOfNeuron) { 0.0 })

                override fun createDelta(input: IOType): IOType0d = IOType0d(MutableList(numOfNeuron) { 0.0 })
            },
        )
}

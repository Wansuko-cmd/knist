@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.output.layer0d

import common.sigmoid
import exception.DomainException
import layers.IOType
import layers.Layer
import kotlin.random.Random

class Sigmoid0d(
    private val numOfNeuron: Int,
    private val type: (numOfNeuron: Int, activationFunction: (Double) -> Double) -> Layer<IOType.IOType0d>,
) : Output0dLayer {
    override fun toLayer() = listOf(
        type(numOfNeuron, ::sigmoid),
        object : Layer<IOType.IOType0d> {
            override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }

            override inline fun forward(
                input: IOType,
                output: IOType,
                weight: Array<IOType>,
            ) {
                val inputArray = input.asIOType0d().value
                val outputArray = output.asIOType0d().value
                for (i in inputArray.indices) {
                    outputArray[i] = inputArray[i]
                }
            }

            override inline fun calcDelta(
                beforeDelta: DoubleArray,
                beforeOutput: IOType,
                delta: DoubleArray,
                weight: Array<IOType>,
            ) {
                val beforeOutputArray = beforeOutput.asIOType0d().value
                for (i in beforeDelta.indices) {
                    val y = beforeOutputArray[i]
                    beforeDelta[i] = (y - delta[i]) * (1 - y) * y
                }
            }

            override inline fun backward(
                weight: Array<IOType>,
                delta: DoubleArray,
                input: IOType,
                rate: Double,
            ) = Unit

            override fun createWeight(input: IOType, random: Random): Array<IOType> =
                Array(input.asIOType0d().value.size) {
                    IOType.IOType0d(DoubleArray(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
                }

            override fun createOutput(input: IOType): IOType.IOType0d =
                IOType.IOType0d(DoubleArray(numOfNeuron) { 0.0 })

            override fun createDelta(input: IOType): DoubleArray = DoubleArray(numOfNeuron) { 0.0 }
        }
    )
}

@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.bias

import layers.IOType
import layers.Layer
import kotlin.random.Random

class Bias0d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType.IOType0d> {
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType0d().value
        val outputArray = output.asIOType0d().value
        for (index in inputArray.indices) {
            outputArray[index] = activationFunction(inputArray[index] + weight[index].asIOType0d().value[index])
        }
    }

    override inline fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    ) {
        beforeDelta.copyInto(delta)
    }

    override inline fun backward(
        weight: Array<IOType>,
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType0d().value
        for (index in weight.indices) {
            weight[index].asIOType0d().value[index] -= rate * delta[index] * inputArray[index]
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType0d().value.size) {
            IOType.IOType0d(DoubleArray(input.asIOType0d().value.size) { random.nextDouble(-1.0, 1.0) })
        }

    override fun createOutput(input: IOType): IOType.IOType0d =
        IOType.IOType0d(DoubleArray(input.asIOType0d().value.size))

    override fun createDelta(input: IOType): DoubleArray = DoubleArray(input.asIOType0d().value.size)
}

@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.bias

import common.iotype.IOType
import common.iotype.IOType0d
import layers.Layer
import kotlin.random.Random

class Bias0d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType0d> {
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType0d()
        val outputArray = output.asIOType0d()
        for (index in inputArray.indices) {
            outputArray[index] = activationFunction(inputArray[index] + weight[index].asIOType0d()[index])
        }
    }

    override inline fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        beforeDelta.asIOType0d().inner = delta.asIOType0d().inner
    }

    override inline fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) {
        val deltaArray = delta.asIOType0d()
        val inputArray = input.asIOType0d()
        for (index in weight.indices) {
            weight[index].asIOType0d()[index] -= rate * deltaArray[index] * inputArray[index]
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType0d().size) {
            IOType0d(MutableList(input.asIOType0d().size) { random.nextDouble(-1.0, 1.0) })
        }

    override fun createOutput(input: IOType): IOType0d =
        IOType0d(MutableList(input.asIOType0d().size) { 0.0 })

    override fun createDelta(input: IOType): IOType0d = IOType0d(MutableList(input.asIOType0d().size) { 0.0 })
}

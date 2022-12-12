package layers.bias

import common.iotype.IOType
import common.iotype.IOType1d
import layers.Layer
import kotlin.random.Random

class Bias1d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType1d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType1d().value
        val outputArray = output.asIOType1d().value
        for (channel in inputArray.indices) {
            for (time in inputArray[channel].indices) {
                outputArray[channel][time] =
                    activationFunction(inputArray[channel][time] + weight[channel].asIOType1d().value[channel][time])
            }
        }
    }

    override fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    ) {
        beforeDelta.copyInto(delta)
    }

    override fun backward(
        weight: Array<IOType>,
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d().value
        var deltaIndex = 0
        for (channel in weight.indices) {
            val weightArray = weight[channel].asIOType1d().value[channel]
            for (time in weightArray.indices) {
                weightArray[time] -= rate * delta[deltaIndex++] * inputArray[channel][time]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType1d(
                Array(input.asIOType1d().value.size) {
                    DoubleArray(input.asIOType1d().value[it].size) { random.nextDouble(-1.0, 1.0) }
                }
            )
        }

    override fun createOutput(input: IOType): IOType1d =
        IOType1d(Array(input.asIOType1d().value.size) { DoubleArray(input.asIOType1d().value[it].size) })

    override fun createDelta(input: IOType): DoubleArray =
        DoubleArray(input.asIOType0d().value.size)
}

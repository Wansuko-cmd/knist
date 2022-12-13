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
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        beforeDelta.asIOType0d().value.copyInto(delta.asIOType0d().value)
    }

    override fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d().value
        val deltaArray = delta.asIOType1d().value
        for (channel in weight.indices) {
            val weightArray = weight[channel].asIOType1d().value[channel]
            for (time in weightArray.indices) {
                weightArray[time] -= rate * deltaArray[channel][time] * inputArray[channel][time]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType1d(
                Array(input.asIOType1d().value.size) {
                    DoubleArray(input.asIOType1d().value[it].size) { random.nextDouble(-1.0, 1.0) }
                },
            )
        }

    override fun createOutput(input: IOType): IOType1d =
        IOType1d(Array(input.asIOType1d().value.size) { DoubleArray(input.asIOType1d().value[it].size) })

    override fun createDelta(input: IOType): IOType1d =
        IOType1d(Array(input.asIOType1d().value.size) { DoubleArray(input.asIOType1d().value[it].size) })
}

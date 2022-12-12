package layers.bias

import layers.IOType
import layers.Layer
import kotlin.random.Random

class Bias1d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType.IOType1d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType1d().value
        val outputArray = output.asIOType1d().value
        for (index in inputArray.indices) {
            for (time in inputArray[index].indices) {
                outputArray[index][time] =
                    activationFunction(inputArray[index][time] + weight[index].asIOType1d().value[index][time])
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
        for (index in weight.indices) {
            val weightArray = weight[index].asIOType1d().value[index]
            for (time in weightArray.indices) {
                weightArray[time] -= rate * delta[deltaIndex++] * inputArray[index][time]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType.IOType1d(
                Array(input.asIOType1d().value.size) {
                    DoubleArray(input.asIOType1d().value[it].size) { random.nextDouble(-1.0, 1.0) }
                }
            )
        }

    override fun createOutput(input: IOType): IOType.IOType1d =
        IOType.IOType1d(Array(input.asIOType1d().value.size) { DoubleArray(input.asIOType1d().value[it].size) })

    override fun createDelta(input: IOType): DoubleArray =
        DoubleArray(input.asIOType0d().value.size)
}

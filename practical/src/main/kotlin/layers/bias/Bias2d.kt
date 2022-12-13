package layers.bias

import common.iotype.IOType
import common.iotype.IOType2d
import layers.Layer
import kotlin.random.Random

class Bias2d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType2d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType2d().value
        val outputArray = output.asIOType2d().value
        for (channel in inputArray.indices) {
            for (row in inputArray[channel].indices) {
                for (column in inputArray[channel][row].indices) {
                    outputArray[channel][row][column] =
                        activationFunction(inputArray[channel][row][column] + weight[channel].asIOType2d().value[channel][row][column])
                }
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
        val deltaArray = delta.asIOType2d().value
        val inputArray = input.asIOType2d().value
        for (channel in weight.indices) {
            val weightArray = weight[channel].asIOType2d().value[channel]
            for (row in weightArray.indices) {
                for (column in weightArray.indices) {
                    weightArray[row][column] -=
                        rate * deltaArray[channel][row][column] * inputArray[channel][row][column]
                }
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType2d().value.size) {
            IOType2d(
                Array(input.asIOType2d().value.size) { index ->
                    Array(input.asIOType2d().value[index].size) { row ->
                        DoubleArray(input.asIOType2d().value[index][row].size) { random.nextDouble(-1.0, 1.0) }
                    }
                },
            )
        }

    override fun createOutput(input: IOType): IOType2d =
        IOType2d(
            Array(input.asIOType2d().value.size) { index ->
                Array(input.asIOType2d().value[index].size) { row ->
                    DoubleArray(input.asIOType2d().value[index][row].size)
                }
            },
        )

    override fun createDelta(input: IOType): IOType2d =
        IOType2d(
            Array(input.asIOType2d().value.size) { index ->
                Array(input.asIOType2d().value[index].size) { row ->
                    DoubleArray(input.asIOType2d().value[index][row].size)
                }
            },
        )
}

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
        for (index in inputArray.indices) {
            for (row in inputArray[index].indices) {
                for (column in inputArray[index][row].indices) {
                    outputArray[index][row][column] =
                        activationFunction(inputArray[index][row][column] + weight[index].asIOType2d().value[index][row][column])
                }
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
        val inputArray = input.asIOType2d().value
        var deltaIndex = 0
        for (index in weight.indices) {
            val weightArray = weight[index].asIOType2d().value[index]
            for (row in weightArray.indices) {
                for (column in weightArray.indices) {
                    weightArray[row][column] -= rate * delta[deltaIndex++] * inputArray[index][row][column]
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
                }
            )
        }

    override fun createOutput(input: IOType): IOType2d =
        IOType2d(
            Array(input.asIOType2d().value.size) { index ->
                Array(input.asIOType2d().value[index].size) { row ->
                    DoubleArray(input.asIOType2d().value[index][row].size)
                }
            }
        )

    override fun createDelta(input: IOType): DoubleArray =
        DoubleArray(input.asIOType0d().value.size)
}

@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.bias

import common.iotype.IOType
import common.iotype.IOType1d
import layers.Layer
import kotlin.random.Random

class Bias1d(
    override val activationFunction: (Double) -> Double,
) : Layer<IOType1d> {
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType1d()
        val outputArray = output.asIOType1d()
        for (channel in inputArray.indices) {
            val weightArray = weight[channel].asIOType1d()[channel]
            for (time in inputArray[channel].indices) {
                outputArray[channel][time] =
                    activationFunction(inputArray[channel][time] + weightArray[time])
            }
        }
    }

    override fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        beforeDelta.asIOType0d().inner = delta.asIOType0d().inner
    }

    override fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d()
        val deltaArray = delta.asIOType1d()
        for (channel in weight.indices) {
            val weightArray = weight[channel].asIOType1d()[channel]
            for (time in weightArray.indices) {
                weightArray[time] -= rate * deltaArray[channel][time] * inputArray[channel][time]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().indexSize) {
            IOType1d.create(
                MutableList(input.asIOType1d().indexSize) {
                    MutableList(input.asIOType1d().timeSize) { random.nextDouble(-1.0, 1.0) }
                },
            )
        }

    override fun createOutput(input: IOType): IOType1d =
        IOType1d.create(MutableList(input.asIOType1d().indexSize) { MutableList(input.asIOType1d().timeSize) { 0.0 } })

    override fun createDelta(input: IOType): IOType1d =
        IOType1d.create(MutableList(input.asIOType1d().indexSize) { MutableList(input.asIOType1d().timeSize) { 0.0 } })
}

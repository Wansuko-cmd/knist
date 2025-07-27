package layers

import common.iotype.IOType
import kotlin.random.Random

interface Layer<T : IOType> {
    val activationFunction: (Double) -> Double

    fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    )
    fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    )
    fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    )
    fun createWeight(input: IOType, random: Random): Array<IOType>
    fun createOutput(input: IOType): T
    fun createDelta(input: IOType): T
}

package layers

import kotlin.random.Random

interface Layer<T : IOType> {
    val activationFunction: (Double) -> Double

    fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    )
    fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    )
    fun backward(
        weight: Array<IOType>,
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    )
    fun createWeight(input: IOType, random: Random): Array<IOType>
    fun createOutput(input: IOType): T
    fun createDelta(input: IOType): DoubleArray
}

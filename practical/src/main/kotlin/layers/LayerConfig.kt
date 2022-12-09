package layers

import kotlin.random.Random

interface LayerConfig<T : IOType> {
    val numOfNeuron: Int
    val activationFunction: (Double) -> Double
    val type: LayerType
    fun createWeight(input: IOType, random: Random): Array<IOType>
    fun createOutput(input: IOType): T
    fun createDelta(input: IOType): Array<Double>
}

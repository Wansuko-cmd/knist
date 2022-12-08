package layers

import kotlin.random.Random

interface LayerConfig<T : IOType> {
    val numOfNeuron: Int
    val activationFunction: (Double) -> Double
    val type: LayerType
    fun createWeight(random: Random, input: IOType): Array<IOType>
    fun createOutput(): T
}

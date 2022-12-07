package layers

import kotlin.random.Random

interface LayerConfig<T : IOType> {
    val numOfNeuron: Int
    val activationFunction: (Double) -> Double
    fun createWeight(random: Random): T
    fun createOutput(): T
}

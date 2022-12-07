package layers.layer0d

import layers.LayerConfig
import kotlin.random.Random

class Layer0dConfig(
    override val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
    val type: Layer0dType,
) : LayerConfig {
    fun createWeight(random: Random) = random.nextDouble(-1.0, 1.0)

    fun createOutput(): Array<Double> = Array(numOfNeuron) { 0.0 }
}

interface Layer0dType {
    fun forward(
        input: Array<Double>,
        output: Array<Double>,
        weight: Array<Array<Double>>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        delta: Array<Double>,
        output: Array<Double>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<Double>>,
    )
    fun backward(
        weight: Array<Array<Double>>,
        delta: Array<Double>,
        input: Array<Double>,
        rate: Double,
    )
}

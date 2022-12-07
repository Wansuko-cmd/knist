package layers.layer1d

import layers.LayerConfig
import kotlin.random.Random

data class Layer1dConfig(
    val channel: Int,
    val kernelSize: Int,
    override val activationFunction: (Double) -> Double,
    val type: Layer1dType,
) : LayerConfig {
    override val numOfNeuron = channel
    fun createWeight(random: Random) = Array(kernelSize) { random.nextDouble(-1.0, 1.0) }
}

interface Layer1dType {
    fun forward(
        input: Array<Array<Double>>,
        output: Array<Array<Double>>,
        weight: Array<Array<Array<Double>>>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        delta: Array<Double>,
        output: Array<Array<Double>>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<Double>>,
    )
    fun backward(
        weight: Array<Array<Array<Double>>>,
        delta: Array<Double>,
        input: Array<Array<Double>>,
        rate: Double,
    )
}

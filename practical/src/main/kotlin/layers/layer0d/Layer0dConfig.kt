package layers.layer0d

import layers.IOType
import layers.LayerConfig
import kotlin.random.Random

class Layer0dConfig(
    override val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
    val type: Layer0dType,
) : LayerConfig {
    fun createWeight(random: Random): IOType =
        IOType.IOType0d(Array(numOfNeuron) { random.nextDouble(-1.0, 1.0) })

    fun createOutput(): IOType = IOType.IOType0d(Array(numOfNeuron) { 0.0 })
}

interface Layer0dType {
    fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        delta: Array<Double>,
        output: IOType,
        afterDelta: Array<Double>,
        afterWeight: Array<IOType>,
    )
    fun backward(
        weight: Array<IOType>,
        delta: Array<Double>,
        input: IOType,
        rate: Double,
    )
}

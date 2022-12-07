package layers.layer0d

import layers.LayerConfig
import kotlin.random.Random
import layers.IOType

class Layer0dConfig(
    override val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
    val type: Layer0dType,
) : LayerConfig {
    fun createWeight(random: Random): IOType = IOType.IOType0d(random.nextDouble(-1.0, 1.0))

    fun createOutput(): Array<IOType> = Array(numOfNeuron) { IOType.IOType0d(0.0) }
}

interface Layer0dType {
    fun forward(
        input: Array<IOType>,
        output: Array<IOType>,
        weight: Array<Array<IOType>>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        delta: Array<Double>,
        output: Array<IOType>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<IOType>>,
    )
    fun backward(
        weight: Array<Array<IOType>>,
        delta: Array<Double>,
        input: Array<IOType>,
        rate: Double,
    )
}

package layers.layer1d

import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

data class Layer1dConfig(
    val channel: Int,
    val inputSize: Int,
    val kernelSize: Int,
    override val activationFunction: (Double) -> Double,
    override val type: LayerType,
) : LayerConfig<IOType.IOType1d> {
    override val numOfNeuron = channel
    override fun createWeight(random: Random, input: IOType): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType.IOType1d(Array(channel) { Array(kernelSize) { random.nextDouble(-1.0, 1.0) } })
        }

    override fun createOutput(): IOType.IOType1d =
        IOType.IOType1d(Array(channel) { Array(inputSize - kernelSize + 1) { 0.0 } })
}

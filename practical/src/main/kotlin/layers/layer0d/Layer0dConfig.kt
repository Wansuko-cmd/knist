package layers.layer0d

import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

class Layer0dConfig(
    override val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
    override val type: LayerType,
) : LayerConfig<IOType.IOType0d> {
    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType0d().value.size) {
            IOType.IOType0d(Array(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
        }

    override fun createOutput(input: IOType): IOType.IOType0d = IOType.IOType0d(Array(numOfNeuron) { 0.0 })
    override fun createDelta(input: IOType): Array<Double> = Array(numOfNeuron) { 0.0 }
}

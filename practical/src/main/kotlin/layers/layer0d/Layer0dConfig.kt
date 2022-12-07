package layers.layer0d

import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

class Layer0dConfig(
    override val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
    val type: LayerType,
) : LayerConfig<IOType.IOType0d> {
    override fun createWeight(random: Random): IOType.IOType0d =
        IOType.IOType0d(Array(numOfNeuron) { random.nextDouble(-1.0, 1.0) })

    override fun createOutput(): IOType.IOType0d = IOType.IOType0d(Array(numOfNeuron) { 0.0 })
}

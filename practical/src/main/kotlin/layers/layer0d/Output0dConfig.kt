@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer0d

import layers.IOType
import layers.LayerType

data class Output0dConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
) {
    fun toLayoutConfig() =
        Layer0dConfig(
            numOfNeuron = size,
            activationFunction = activationFunction,
            type = object : LayerType by Affine {
                override inline fun calcDelta(
                    delta: Array<Double>,
                    output: IOType,
                    afterDelta: Array<Double>,
                    afterWeight: Array<IOType>,
                ) {
                    val outputArray = output.asIOType0d().value
                    for (i in delta.indices) {
                        val y = outputArray[i]
                        delta[i] = (y - afterDelta[i]) * (1 - y) * y
                    }
                }
            },
        )
}

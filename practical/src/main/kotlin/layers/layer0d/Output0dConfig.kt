package layers.layer0d

import layers.IOType

data class Output0dConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
) {
    fun toLayoutConfig() =
        Layer0dConfig(
            numOfNeuron = size,
            activationFunction = activationFunction,
            type = object : Layer0dType by Affine {
                override inline fun calcDelta(
                    delta: Array<Double>,
                    output: Array<IOType>,
                    afterDelta: Array<Double>,
                    afterWeight: Array<Array<IOType>>,
                ) {
                    for (i in delta.indices) {
                        val y = output[i].asIOType0d().value
                        delta[i] = (y - afterDelta[i]) * (1 - y) * y
                    }
                }
            },
        )
}

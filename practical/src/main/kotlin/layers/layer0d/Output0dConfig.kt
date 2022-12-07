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
                    output: IOType,
                    afterDelta: Array<Double>,
                    afterWeight: Array<IOType>,
                ) {
                    val output = output.asIOType0d().value
                    for (i in delta.indices) {
                        val y = output[i]
                        delta[i] = (y - afterDelta[i]) * (1 - y) * y
                    }
                }
            },
        )
}

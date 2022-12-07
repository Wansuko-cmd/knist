package layers

import layers.affine.Affine

data class OutputConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
) {
    fun toLayoutConfig() =
        LayerConfig(
            size = size,
            activationFunction = activationFunction,
            type = object : LayerType by Affine {
                override inline fun calcDelta(
                    delta: Array<Double>,
                    output: Array<Double>,
                    afterDelta: Array<Double>,
                    afterWeight: Array<Array<Double>>,
                ) {
                    for (i in delta.indices) {
                        val y = output[i]
                        delta[i] = (y - afterDelta[i]) * (1 - y) * y
                    }
                }
            },
        )
}

package layers

data class LayerConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
    val type: LayerType,
)

interface LayerType {
    fun forward(
        input: Array<Double>,
        output: Array<Double>,
        weight: Array<Array<Double>>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        delta: Array<Double>,
        output: Array<Double>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<Double>>,
    )
    fun backward(
        weight: Array<Array<Double>>,
        delta: Array<Double>,
        input: Array<Double>,
        rate: Double,
    )
}

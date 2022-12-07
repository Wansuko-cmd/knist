package layers

interface LayerType {
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

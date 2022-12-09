package layers

interface LayerType {
    fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double,
    )
    fun calcDelta(
        beforeDelta: Array<Double>,
        beforeOutput: IOType,
        delta: Array<Double>,
        weight: Array<IOType>,
    )
    fun backward(
        weight: Array<IOType>,
        delta: Array<Double>,
        input: IOType,
        rate: Double,
    )
}

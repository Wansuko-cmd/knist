package layers.layer1d

import exception.DomainException
import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

data class Input1dConfig(val channel: Int, val inputSize: Int) : LayerConfig<IOType.IOType1d> {
    override val numOfNeuron: Int = channel
    override val numOfOutput: Int = channel * inputSize
    override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }
    override val type: LayerType = object : LayerType {
        override fun forward(
            input: IOType,
            output: IOType,
            weight: Array<IOType>,
            activationFunction: (Double) -> Double,
        ) = throw DomainException.UnreachableCodeException()
        override fun calcDelta(
            delta: Array<Double>,
            output: IOType,
            afterDelta: Array<Double>,
            afterWeight: Array<IOType>,
        ) = throw DomainException.UnreachableCodeException()
        override fun backward(
            weight: Array<IOType>,
            delta: Array<Double>,
            input: IOType,
            rate: Double,
        ) = throw DomainException.UnreachableCodeException()
    }
    override fun createWeight(random: Random, input: IOType): Array<IOType> = throw DomainException.UnreachableCodeException()
    override fun createOutput() = IOType.IOType1d(Array(channel) { Array(inputSize) { 0.0 } })
}

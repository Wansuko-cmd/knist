package layers.layer0d

import exception.DomainException
import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

data class Input0dConfig(val size: Int) : LayerConfig<IOType.IOType0d> {
    override val numOfNeuron: Int = size
    override val numOfOutput: Int = size
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
    override fun createWeight(random: Random) = throw DomainException.UnreachableCodeException()
    override fun createOutput() = IOType.IOType0d(Array(numOfNeuron) { 0.0 })
}

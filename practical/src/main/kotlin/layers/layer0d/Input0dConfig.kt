package layers.layer0d

import exception.DomainException
import layers.IOType
import layers.LayerConfig
import layers.LayerType
import kotlin.random.Random

data class Input0dConfig(val size: Int) : LayerConfig<IOType.IOType0d> {
    override val numOfNeuron: Int = size
    override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }
    override val type: LayerType = object : LayerType {
        override fun forward(
            input: IOType,
            output: IOType,
            weight: Array<IOType>,
            activationFunction: (Double) -> Double,
        ) = throw DomainException.UnreachableCodeException()
        override fun calcDelta(
            beforeDelta: Array<Double>,
            beforeOutput: IOType,
            delta: Array<Double>,
            weight: Array<IOType>,
        ) = throw DomainException.UnreachableCodeException()
        override fun backward(
            weight: Array<IOType>,
            delta: Array<Double>,
            input: IOType,
            rate: Double,
        ) = throw DomainException.UnreachableCodeException()
    }
    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        throw DomainException.UnreachableCodeException()
    override fun createOutput(input: IOType): IOType.IOType0d = IOType.IOType0d(Array(numOfNeuron) { 0.0 })
    override fun createDelta(input: IOType): Array<Double> = Array(numOfNeuron) { 0.0 }
}

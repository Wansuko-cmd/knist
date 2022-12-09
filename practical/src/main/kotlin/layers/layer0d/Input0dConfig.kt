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
            beforeDelta: DoubleArray,
            beforeOutput: IOType,
            delta: DoubleArray,
            weight: Array<IOType>,
        ) = throw DomainException.UnreachableCodeException()
        override fun backward(
            weight: Array<IOType>,
            delta: DoubleArray,
            input: IOType,
            rate: Double,
        ) = throw DomainException.UnreachableCodeException()
    }
    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        throw DomainException.UnreachableCodeException()
    override fun createOutput(input: IOType): IOType.IOType0d = IOType.IOType0d(DoubleArray(numOfNeuron) { 0.0 })
    override fun createDelta(input: IOType): DoubleArray = DoubleArray(numOfNeuron) { 0.0 }
}

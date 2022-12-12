package layers.input

import exception.DomainException
import layers.IOType
import layers.Layer
import kotlin.random.Random

data class Input0dLayer(val size: Int) : Layer<IOType.IOType0d> {
    override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
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
    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        throw DomainException.UnreachableCodeException()
    override fun createOutput(input: IOType): IOType.IOType0d = IOType.IOType0d(DoubleArray(size) { 0.0 })
    override fun createDelta(input: IOType): DoubleArray = DoubleArray(size) { 0.0 }
}

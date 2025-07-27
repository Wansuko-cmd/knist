package layers.input

import exception.DomainException
import common.iotype.IOType
import common.iotype.IOType0d
import layers.Layer
import kotlin.random.Random

data class Input0dLayer(val size: Int) : Layer<IOType0d> {
    override val activationFunction: (Double) -> Double = { throw DomainException.UnreachableCodeException() }
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) = throw DomainException.UnreachableCodeException()
    override fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) = throw DomainException.UnreachableCodeException()
    override fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) = throw DomainException.UnreachableCodeException()
    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        throw DomainException.UnreachableCodeException()
    override fun createOutput(input: IOType): IOType0d = IOType0d(MutableList(size) { 0.0 })
    override fun createDelta(input: IOType): IOType0d = IOType0d(MutableList(size) { 0.0 })
}

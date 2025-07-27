package layers.input

import common.iotype.IOType
import common.iotype.IOType1d
import exception.DomainException
import layers.Layer
import kotlin.random.Random

data class Input1dLayer(val channel: Int, val inputSize: Int) : Layer<IOType1d> {
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
    override fun createOutput(input: IOType): IOType1d =
        IOType1d.create(MutableList(channel) { MutableList(inputSize) { 0.0 } })

    // 実際にこのdelta配列が使われることはない
    override fun createDelta(input: IOType): IOType1d =
        IOType1d.create(MutableList(channel) { MutableList(inputSize) { 0.0 } })
}

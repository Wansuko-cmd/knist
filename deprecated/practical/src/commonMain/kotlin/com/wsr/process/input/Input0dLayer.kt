package com.wsr.process.input

import com.wsr.DomainException
import com.wsr.common.iotype.IOType
import com.wsr.common.iotype.IOType0d
import com.wsr.process.Layer
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

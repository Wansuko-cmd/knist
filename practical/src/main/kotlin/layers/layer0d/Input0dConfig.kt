package layers.layer0d

import exception.DomainException
import layers.IOType

data class Input0dConfig(val size: Int) {
    fun toLayoutConfig() =
        Layer0dConfig(
            numOfNeuron = size,
            activationFunction = { throw DomainException.UnreachableCodeException() },
            type = object : Layer0dType {
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
                override fun backward(weight: Array<IOType>, delta: Array<Double>, input: IOType, rate: Double) = throw DomainException.UnreachableCodeException()
            },
        )
}

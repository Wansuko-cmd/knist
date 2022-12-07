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
                    input: Array<IOType>,
                    output: Array<IOType>,
                    weight: Array<Array<IOType>>,
                    activationFunction: (Double) -> Double,
                ) = throw DomainException.UnreachableCodeException()
                override fun calcDelta(
                    delta: Array<Double>,
                    output: Array<IOType>,
                    afterDelta: Array<Double>,
                    afterWeight: Array<Array<IOType>>,
                ) = throw DomainException.UnreachableCodeException()
                override fun backward(
                    weight: Array<Array<IOType>>,
                    delta: Array<Double>,
                    input: Array<IOType>,
                    rate: Double,
                ) = throw DomainException.UnreachableCodeException()
            },
        )
}

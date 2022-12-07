package layers.layer0d

import exception.DomainException

data class Input0dConfig(val size: Int) {
    fun toLayoutConfig() =
        Layer0dConfig(
            numOfNeuron = size,
            activationFunction = { throw DomainException.UnreachableCodeException() },
            type = object : Layer0dType {
                override fun forward(
                    input: Array<Double>,
                    output: Array<Double>,
                    weight: Array<Array<Double>>,
                    activationFunction: (Double) -> Double,
                ) = throw DomainException.UnreachableCodeException()
                override fun calcDelta(
                    delta: Array<Double>,
                    output: Array<Double>,
                    afterDelta: Array<Double>,
                    afterWeight: Array<Array<Double>>,
                ) = throw DomainException.UnreachableCodeException()
                override fun backward(
                    weight: Array<Array<Double>>,
                    delta: Array<Double>,
                    input: Array<Double>,
                    rate: Double,
                ) = throw DomainException.UnreachableCodeException()
            },
        )
}

package layers

import exception.DomainException

//data class InputConfig(val size: Int) {
//    fun toLayoutConfig() =
//        LayerConfig(
//            size = size,
//            activationFunction = { throw DomainException.UnreachableCodeException() },
//            type = object : LayerType {
//                override fun forward(
//                    input: Array<Double>,
//                    output: Array<Double>,
//                    weight: Array<Array<Double>>,
//                    activationFunction: (Double) -> Double,
//                ) = throw DomainException.UnreachableCodeException()
//                override fun calcDelta(
//                    delta: Array<Double>,
//                    output: Array<Double>,
//                    afterDelta: Array<Double>,
//                    afterWeight: Array<Array<Double>>,
//                ) = throw DomainException.UnreachableCodeException()
//                override fun backward(
//                    weight: Array<Array<Double>>,
//                    delta: Array<Double>,
//                    input: Array<Double>,
//                    rate: Double,
//                ) = throw DomainException.UnreachableCodeException()
//            },
//        )
//}

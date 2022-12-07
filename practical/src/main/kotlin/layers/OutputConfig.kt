package layers

//import layers.affine.Affine
import layers.layer0d.Affine
import layers.layer0d.Layer0dConfig
import layers.layer0d.Layer0dType

//data class OutputConfig(
//    val size: Int,
//    val activationFunction: (Double) -> Double,
//) {
//    fun toLayoutConfig() =
//        Layer0dConfig(
//            numOfNeuron = size,
//            activationFunction = activationFunction,
//            type = object : Layer0dType by Affine {
//                override inline fun calcDelta(
//                    delta: Array<Double>,
//                    output: Array<Double>,
//                    afterDelta: Array<Double>,
//                    afterWeight: Array<Array<Double>>,
//                ) {
//                    for (i in delta.indices) {
//                        val y = output[i]
//                        delta[i] = (y - afterDelta[i]) * (1 - y) * y
//                    }
//                }
//            },
//        )
//}

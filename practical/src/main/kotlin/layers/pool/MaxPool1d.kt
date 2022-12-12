//@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
//
//package layers.layer1d
//
//import common.step
//import common.iotype.IOType
//
//object MaxPool1d : LayerType {
//
//    // TODO: 最大の値を取得できたニューロンのWeightを1にする
//    override inline fun forward(
//        input: IOType,
//        output: IOType,
//        weight: Array<IOType>,
//        activationFunction: (Double) -> Double,
//    ) {
//        val inputArray = input.asIOType1d().value
//        val outputArray = output.asIOType1d().value
//        for (inputIndex in inputArray.indices) {
//            for (outputIndex in outputArray.indices) {
//                outputArray[outputIndex] = inputArray[inputIndex].copyOfRange(outputIndex, outputIndex + 3)
//                weight[inputIndex].asIOType1d().value[outputIndex]
//            }
//        }
//    }
//
//    override inline fun calcDelta(
//        beforeDelta: DoubleArray,
//        beforeOutput: IOType,
//        delta: DoubleArray,
//        weight: Array<IOType>,
//    ) {
//        // 畳み込みの出力ニューロンを一列にした時のindexを表す
//        var index = 0
//        val outputArray = beforeOutput.asIOType1d().value
//
//        for (i in beforeDelta.indices) {
//            var sum = 0.0
//            val afterWeightArray = weight[i].asIOType0d().value
//            for (t in outputArray[i].indices) {
//                sum += step(outputArray[i][t]) * (0 until weight[index++].asIOType0d().value.size)
//                    .sumOf { delta[it] * afterWeightArray[it] }
//            }
//            beforeDelta[i] = sum
//        }
//    }
//
//    override inline fun backward(
//        weight: Array<IOType>,
//        delta: DoubleArray,
//        input: IOType,
//        rate: Double,
//    ) = Unit
//}

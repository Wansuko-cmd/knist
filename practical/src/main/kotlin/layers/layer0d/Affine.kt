package layers.layer0d

import common.step
import layers.IOType

object Affine : Layer0dType {
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double,
    ) {
        val output = output.asIOType0d().value
        val input = input.asIOType0d().value
        for (outputIndex in output.indices) {
            var sum = 0.0
            for (inputIndex in input.indices) {
                sum += input[inputIndex] * weight[inputIndex].asIOType0d().value[outputIndex]
            }
            output[outputIndex] = activationFunction(sum)
        }
    }

    /**
     * delta -> 計算結果を格納するdelta
     * output -> 自分の層の出力
     * afterDelta -> 後ろの層のdelta
     * afterWeight -> 自分と後ろの層の重み Array[前の層のニューロン][後ろの層のニューロン]
     */
    override inline fun calcDelta(
        delta: Array<Double>,
        output: IOType,
        afterDelta: Array<Double>,
        afterWeight: Array<IOType>,
    ) {
        val output = output.asIOType0d().value
        for (i in delta.indices) {
            delta[i] = step(output[i]) * (0 until afterWeight[i].asIOType0d().value.size)
                .sumOf { afterDelta[it] * afterWeight[i].asIOType0d().value[it] }
        }
    }

    override inline fun backward(weight: Array<IOType>, delta: Array<Double>, input: IOType, rate: Double) {
        val input = input.asIOType0d().value
        for (before in weight.indices) {
            for (after in weight[before].asIOType0d().value.indices) {
                weight[before].asIOType0d().value[after] -= rate * delta[after] * input[before]
            }
        }
    }
}

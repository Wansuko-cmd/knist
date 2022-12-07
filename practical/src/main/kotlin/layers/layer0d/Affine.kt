package layers.layer0d

import common.step
import layers.IOType

object Affine : Layer0dType {
    override inline fun forward(
        input: Array<IOType>,
        output: Array<IOType>,
        weight: Array<Array<IOType>>,
        activationFunction: (Double) -> Double
    ) {
        for (outputIndex in output.indices) {
            var sum = 0.0
            for (inputIndex in input.indices) {
                sum += input[inputIndex].asIOType0d().value * weight[inputIndex][outputIndex].asIOType0d().value
            }
            output[outputIndex] = IOType.IOType0d(activationFunction(sum))
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
        output: Array<IOType>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<IOType>>
    ) {
        for (i in delta.indices) {
            delta[i] = step(output[i].asIOType0d().value) * (0 until afterWeight[i].size)
                .sumOf { afterDelta[it] * afterWeight[i][it].asIOType0d().value }
        }
    }

    override inline fun backward(
        weight: Array<Array<IOType>>,
        delta: Array<Double>,
        input: Array<IOType>,
        rate: Double
    ) {
        for (before in weight.indices) {
            for (after in weight[before].indices) {
                weight[before][after] = IOType.IOType0d(weight[before][after].asIOType0d().value - rate * delta[after] * input[before].asIOType0d().value)
            }
        }
    }
}

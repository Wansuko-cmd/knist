package layers.affine

import common.step
import layers.LayerType

object Affine : LayerType {
    override inline fun forward(
        input: Array<Double>,
        output: Array<Double>,
        weight: Array<Array<Double>>,
        activationFunction: (Double) -> Double,
    ) {
        for (outputIndex in output.indices) {
            var sum = 0.0
            for (inputIndex in input.indices) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
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
        output: Array<Double>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<Double>>,
    ) {
        for (i in delta.indices) {
            delta[i] = step(output[i]) * (0 until afterWeight[i].size)
                .sumOf { afterDelta[it] * afterWeight[i][it] }
        }
    }

    override inline fun backward(
        weight: Array<Array<Double>>,
        delta: Array<Double>,
        input: Array<Double>,
        rate: Double,
    ) {
        for (before in weight.indices) {
            for (after in weight[before].indices) {
                weight[before][after] -= rate * delta[after] * input[before]
            }
        }
    }
}

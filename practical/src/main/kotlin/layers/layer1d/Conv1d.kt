package layers.layer1d

import common.step

object Conv1d : Layer1dType {
    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun forward(
        input: Array<Array<Double>>,
        output: Array<Array<Double>>,
        weight: Array<Array<Array<Double>>>,
        activationFunction: (Double) -> Double,
    ) {
        for (outputChannel in output.indices) {
            for (i in output[outputChannel].indices) { output[outputChannel][i] = 0.0 }
            for (inputChannel in input.indices) {
                input[inputChannel].conv1d(
                    kernel = weight[inputChannel][outputChannel],
                    output = output[outputChannel],
                    activationFunction = activationFunction,
                )
            }
        }
    }

    override inline fun calcDelta(
        delta: Array<Double>,
        output: Array<Array<Double>>,
        afterDelta: Array<Double>,
        afterWeight: Array<Array<Double>>,
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var index = 0

        for (i in delta.indices) {
            var sum = 0.0
            for (t in output[i].indices) {
                sum += step(output[i][t]) * (0 until afterWeight[index++].size)
                    .sumOf { afterDelta[it] * afterWeight[i][it] }
            }
            delta[i] = sum
        }
    }

    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun backward(
        weight: Array<Array<Array<Double>>>,
        delta: Array<Double>,
        input: Array<Array<Double>>,
        rate: Double,
    ) {
        for (inputChannel in weight.indices) {
            for (outputChannel in weight[inputChannel].indices) {
                for (t in weight[inputChannel][outputChannel].indices) {
                    var sum = 0.0
                    val kernelSize = input[inputChannel].size - weight[inputChannel][outputChannel].size + 1
                    for (outputIndex in 0 until kernelSize) {
                        sum += input[inputChannel][t + outputIndex]
                    }
                    weight[inputChannel][outputChannel][t] -= rate * delta[outputChannel] * sum
                }
            }
        }
    }
}

inline fun Array<Double>.conv1d(
    kernel: Array<Double>,
    output: Array<Double>,
    activationFunction: (Double) -> Double,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        for (kernelIndex in kernel.indices) {
            sum += this[outputIndex + kernelIndex] * kernel[kernelIndex]
        }
        output[outputIndex] += activationFunction(sum)
    }
}

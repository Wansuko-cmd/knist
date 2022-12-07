package layers.layer1d

import common.step
import layers.IOType
import layers.LayerType

object Conv1d : LayerType {
    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double
    ) {
        for (outputChannel in output.asIOType1d().value.indices) {
            for (i in output.asIOType1d().value[outputChannel].indices) { output.asIOType1d().value[outputChannel][i] = 0.0 }
            for (inputChannel in input.asIOType1d().value.indices) {
                input.asIOType1d().value[inputChannel].conv1d(
                    kernel = weight[inputChannel].asIOType1d().value[outputChannel],
                    output = output.asIOType1d().value[outputChannel],
                    activationFunction = activationFunction,
                )
            }
        }
    }

    override inline fun calcDelta(
        delta: Array<Double>,
        output: IOType,
        afterDelta: Array<Double>,
        afterWeight: Array<IOType>
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var index = 0

        for (i in delta.indices) {
            var sum = 0.0
            for (t in output.asIOType1d().value[i].indices) {
                sum += step(output.asIOType1d().value[i][t]) * (0 until afterWeight[index++].asIOType1d().value.size)
                    .sumOf { afterDelta[it] * afterWeight[i].asIOType0d().value[it] }
            }
            delta[i] = sum
        }
    }

    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun backward(
        weight: Array<IOType>,
        delta: Array<Double>,
        input: IOType,
        rate: Double,
    ) {
        for (inputChannel in weight.indices) {
            for (outputChannel in weight[inputChannel].asIOType1d().value.indices) {
                for (t in weight[inputChannel].asIOType1d().value[outputChannel].indices) {
                    var sum = 0.0
                    val kernelSize = input.asIOType1d().value[inputChannel].size - weight[inputChannel].asIOType1d().value[outputChannel].size + 1
                    for (outputIndex in 0 until kernelSize) {
                        sum += input.asIOType1d().value[inputChannel][t + outputIndex]
                    }
                    weight[inputChannel].asIOType1d().value[outputChannel][t] -= rate * delta[outputChannel] * sum
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

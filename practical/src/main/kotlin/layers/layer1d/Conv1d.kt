@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer1d

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
        activationFunction: (Double) -> Double,
    ) {
        val inputArray = input.asIOType1d().value
        val outputArray = output.asIOType1d().value
        for (outputChannel in outputArray.indices) {
            outputArray[outputChannel].fill(0.0)
            for (inputChannel in inputArray.indices) {
                inputArray[inputChannel].conv1d(
                    kernel = weight[inputChannel].asIOType1d().value[outputChannel],
                    output = outputArray[outputChannel],
                    activationFunction = activationFunction,
                )
            }
        }
    }

    override inline fun calcDelta(
        beforeDelta: Array<Double>,
        beforeOutput: IOType,
        delta: Array<Double>,
        weight: Array<IOType>,
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var beforeDeltaIndex = 0
        val beforeOutputArray = beforeOutput.asIOType1d().value

        // deltaの初期化
        beforeDelta.fill(0.0)

        // 入力チャンネル順に計算を行う
        for (inputChannelIndex in beforeOutputArray.indices) {
            val weightArray = weight[inputChannelIndex].asIOType1d().value
            var deltaIndex = 0
            for (outputChannelIndex in weightArray.indices) {
                // 出力信号の大きさ
                val outputSize = beforeOutputArray[inputChannelIndex].size - weightArray[outputChannelIndex].size + 1
                weightArray[outputChannelIndex].deConv1d(
                    kernel = delta.sliceArray(deltaIndex until deltaIndex + outputSize).reversedArray(),
                    output = beforeDelta.sliceArray(beforeDeltaIndex until beforeDeltaIndex + beforeOutputArray[inputChannelIndex].size),
                )
                deltaIndex += outputSize
            }
            beforeDeltaIndex += beforeOutputArray[inputChannelIndex].size
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
        val inputArray = input.asIOType1d().value

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            var index = 0
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                val outputSize = inputArray[inputChannel].size - weightArray[outputChannel].size + 1
                for (time in weightArray[outputChannel].indices) {
                    var sum = 0.0
                    for (outputIndex in 0 until outputSize) {
                        sum += inputArray[inputChannel][time + outputIndex] * delta[index + outputIndex]
                    }
                    weightArray[outputChannel][time] -= rate * sum
                }
                index += outputSize
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

inline fun Array<Double>.deConv1d(
    kernel: Array<Double>,
    output: Array<Double>,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        val expandOutputIndex = outputIndex - kernel.size + 1
        for (kernelIndex in kernel.indices) {
            sum += this.getOrElse(expandOutputIndex + kernelIndex) { 0.0 } * kernel[kernelIndex]
        }
        output[outputIndex] += sum
    }
}

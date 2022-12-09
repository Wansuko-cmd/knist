@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer1d

import common.step
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import layers.IOType
import layers.LayerType

val sp = DoubleVector.SPECIES_MAX

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
                )
            }
            for (outputTime in outputArray[outputChannel].indices) {
                outputArray[outputChannel][outputTime] = activationFunction(outputArray[outputChannel][outputTime])
            }
        }
    }

    override inline fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var beforeDeltaIndex = 0
        val beforeOutputArray = beforeOutput.asIOType1d().value

        // 出力信号の大きさ(どの層の組み合わせでも固定になる)
        val outputSize = beforeOutputArray.first().size - weight.first().asIOType1d().value.first().size + 1

        // deltaの初期化
        beforeDelta.fill(0.0)

        // 入力チャンネル順に計算を行う
        for (inputChannelIndex in beforeOutputArray.indices) {
            val weightArray = weight[inputChannelIndex].asIOType1d().value
            var deltaIndex = 0
            for (outputChannelIndex in weightArray.indices) {
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
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d().value
        // 出力信号の大きさ(どの層の組み合わせでも固定になる)
        val outputSize = inputArray.first().size - weight.first().asIOType1d().value.first().size + 1

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            var outputIndex = 0
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                for (kernelTime in weightArray[outputChannel].indices) {
                    var sum = 0.0
                    for (outputTime in 0 until sp.loopBound(outputSize) step sp.length()) {
                        val i = DoubleVector.fromArray(sp, inputArray[inputChannel], kernelTime + outputTime)
                        val d = DoubleVector.fromArray(sp, delta, outputIndex + outputTime)
                        sum += i.add(d).reduceLanes(VectorOperators.ADD)
                    }
                    weightArray[outputChannel][kernelTime] -= rate * sum
                }
                outputIndex += outputSize
            }
        }
    }
}

inline fun DoubleArray.conv1d(
    kernel: DoubleArray,
    output: DoubleArray,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        for (kernelIndex in 0 until sp.loopBound(kernel.size) step sp.length()) {
            val i = DoubleVector.fromArray(sp, this, outputIndex + kernelIndex)
            val k = DoubleVector.fromArray(sp, kernel, kernelIndex)
            sum += i.add(k).reduceLanes(VectorOperators.MUL)
        }
        output[outputIndex] += sum
    }
}

inline fun DoubleArray.deConv1d(
    kernel: DoubleArray,
    output: DoubleArray,
) {
    val resizedInput = doubleArrayOf(
        *DoubleArray(kernel.size - 1) { 0.0 },
        *this.toTypedArray().toDoubleArray(),
        *DoubleArray(kernel.size - 1) { 0.0 },
    )
    for (outputIndex in output.indices) {
        var sum = 0.0
        for (kernelIndex in 0 until sp.loopBound(kernel.size) step sp.length()) {
            val i = DoubleVector.fromArray(sp, resizedInput, outputIndex + kernelIndex)
            val k = DoubleVector.fromArray(sp, kernel, kernelIndex)
            sum += i.add(k).reduceLanes(VectorOperators.MUL)
        }
        output[outputIndex] += sum
    }
}

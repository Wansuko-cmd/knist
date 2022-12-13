@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.conv

import common.conv1d
import common.deConv1d
import common.innerProduct
import common.iotype.IOType
import common.iotype.IOType1d
import layers.Layer
import kotlin.random.Random

class Conv1d(
    private val channel: Int,
    private val kernelSize: Int,
    private val padding: Int,
    private val stride: Int,
    override val activationFunction: (Double) -> Double,
) : Layer<IOType1d> {

    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType1d().value
        val outputArray = output.asIOType1d().value
        for (outputChannel in outputArray.indices) {
            outputArray[outputChannel].fill(0.0)
            for (inputChannel in inputArray.indices) {
                inputArray[inputChannel].conv1d(
                    kernel = weight[inputChannel].asIOType1d().value[outputChannel],
                    output = outputArray[outputChannel],
                    padding = padding,
                    stride = stride,
                )
            }
            for (outputTime in outputArray[outputChannel].indices) {
                outputArray[outputChannel][outputTime] = activationFunction(outputArray[outputChannel][outputTime])
            }
        }
    }

    override fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        val beforeDeltaArray = beforeDelta.asIOType1d().value
        val beforeOutputArray = beforeOutput.asIOType1d().value

        val deltaArray = delta.asIOType1d().value

        // 入力チャンネル順に計算を行う
        for (inputChannel in beforeOutputArray.indices) {
            beforeDeltaArray[inputChannel].fill(0.0)
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                weightArray[outputChannel].deConv1d(
                    kernel = deltaArray[outputChannel].reversedArray(),
                    output = beforeDeltaArray[inputChannel],
                )
            }
        }
    }

    override fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) {
        val deltaArray = delta.asIOType1d().value
        val inputArray = input.asIOType1d().value

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                for (kernelTime in weightArray[outputChannel].indices) {
                    weightArray[outputChannel][kernelTime] -= rate * deltaArray[outputChannel]
                        .innerProduct(inputArray[inputChannel], kernelTime)
                }
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType1d(Array(channel) { DoubleArray(kernelSize) { random.nextDouble(-1.0, 1.0) } })
        }

    override fun createOutput(input: IOType): IOType1d =
        IOType1d(Array(channel) { DoubleArray(input.asIOType1d().value.first().size - kernelSize + 1 + padding * 2) })

    override fun createDelta(input: IOType): IOType1d =
        IOType1d(Array(channel) { DoubleArray(input.asIOType1d().value.first().size - kernelSize + 1) })
}

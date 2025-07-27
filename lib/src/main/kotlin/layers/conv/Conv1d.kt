@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.conv

import common.iotype.IOType
import common.iotype.IOType0d
import common.iotype.IOType1d
import common.iotype.conv1d
import common.iotype.deConv1d
import common.iotype.innerProduct
import common.iotype.resize
import kotlin.random.Random
import layers.Layer

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
        val inputArray = input.asIOType1d()
        val outputArray = output.asIOType1d()
        for (outputChannel in outputArray.indices) {
            outputArray[outputChannel].inner.fill(0.0)
            for (inputChannel in inputArray.indices) {
                inputArray[inputChannel].conv1d(
                    kernel = weight[inputChannel].asIOType1d()[outputChannel],
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
        val beforeDeltaArray = beforeDelta.asIOType1d()
        val beforeOutputArray = beforeOutput.asIOType1d()

        val deltaArray = delta.asIOType1d()

        // 入力チャンネル順に計算を行う
        for (inputChannel in beforeOutputArray.indices) {
            beforeDeltaArray[inputChannel].inner.fill(0.0)
            val weightArray = weight[inputChannel].asIOType1d()
            for (outputChannel in weightArray.indices) {
                weightArray[outputChannel].deConv1d(
                    kernel = IOType0d(deltaArray[outputChannel].inner.reversed().toMutableList()),
                    output = beforeDeltaArray[inputChannel],
                    padding = padding,
                    stride = stride,
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
        val deltaArray = delta.asIOType1d()
        val inputArray = input.asIOType1d()

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            val weightArray = weight[inputChannel].asIOType1d()
            for (outputChannel in weightArray.indices) {
                for (kernelTime in weightArray[outputChannel].indices) {
                    weightArray[outputChannel][kernelTime] -= rate * deltaArray[outputChannel]
                        .innerProduct(inputArray[inputChannel].resize(padding), kernelTime)
                }
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().indexSize) {
            IOType1d.create(MutableList(channel) { MutableList(kernelSize) { random.nextDouble(-1.0, 1.0) } })
        }

    override fun createOutput(input: IOType): IOType1d =
        IOType1d.create(MutableList(channel) { MutableList(input.asIOType1d().timeSize - kernelSize + 1 + padding * 2) { 0.0 } })

    override fun createDelta(input: IOType): IOType1d =
        IOType1d.create(MutableList(channel) { MutableList(input.asIOType1d().timeSize - kernelSize + 1 + padding * 2) { 0.0 } })
}

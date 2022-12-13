package layers.conv

import common.conv2d
import common.deConv2d
import common.innerProduct
import common.iotype.IOType
import common.iotype.IOType2d
import layers.Layer
import kotlin.random.Random

class Conv2d(
    private val channel: Int,
    private val kernelSize: Int,
    override val activationFunction: (Double) -> Double,
) : Layer<IOType2d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType2d().value
        val outputArray = output.asIOType2d().value

        for (outputChannel in outputArray.indices) {
            // 値の初期化
            for (outputRow in outputArray[outputChannel].indices) {
                outputArray[outputChannel][outputRow].fill(0.0)
            }
            for (inputChannel in inputArray.indices) {
                inputArray[inputChannel].conv2d(
                    kernel = weight[inputChannel].asIOType2d().value[outputChannel],
                    output = outputArray[outputChannel],
                )
            }
            for (outputRow in outputArray[outputChannel].indices) {
                for (outputColumn in outputArray[outputChannel][outputRow].indices) {
                    outputArray[outputChannel][outputRow][outputColumn] =
                        activationFunction(outputArray[outputChannel][outputRow][outputColumn])
                }
            }
        }
    }

    override fun calcDelta(
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        val beforeDeltaArray = beforeDelta.asIOType2d().value
        val beforeOutputArray = beforeOutput.asIOType2d().value

        val deltaArray = delta.asIOType2d().value

        // 入力チャンネル順に計算を行う
        for (inputChannel in beforeOutputArray.indices) {
            for (inputRow in beforeDeltaArray[inputChannel].indices) {
                beforeDeltaArray[inputChannel][inputRow].fill(0.0)
            }
            val weightArray = weight[inputChannel].asIOType2d().value
            for (outputChannel in weightArray.indices) {
                weightArray[outputChannel].deConv2d(
                    kernel = deltaArray[outputChannel].map { it.reversedArray() }.reversed().toTypedArray(),
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
        val deltaArray = delta.asIOType2d().value
        val inputArray = input.asIOType2d().value

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            val weightArray = weight[inputChannel].asIOType2d().value
            for (outputChannel in weightArray.indices) {
                for (kernelRow in weightArray[outputChannel].indices) {
                    for (kernelColumn in weightArray[outputChannel][kernelRow].indices) {
                        weightArray[outputChannel][kernelRow][kernelColumn] -= rate * deltaArray[outputChannel]
                            .innerProduct(inputArray[inputChannel], kernelRow, kernelColumn)
                    }
                }
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType2d().value.size) {
            IOType2d(
                Array(channel) {
                    Array(kernelSize) {
                        DoubleArray(kernelSize) { random.nextDouble(-1.0, 1.0) }
                    }
                },
            )
        }

    override fun createOutput(input: IOType): IOType2d =
        IOType2d(
            Array(channel) {
                Array(input.asIOType2d().value.first().size - kernelSize + 1) {
                    DoubleArray(input.asIOType2d().value.first().first().size - kernelSize + 1)
                }
            }
        )

    override fun createDelta(input: IOType): IOType2d =
        IOType2d(
            Array(channel) {
                Array(input.asIOType2d().value.first().size - kernelSize + 1) {
                    DoubleArray(input.asIOType2d().value.first().first().size - kernelSize + 1)
                }
            }
        )
}

@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.conv

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies
import layers.IOType
import layers.Layer
import kotlin.random.Random

val sp: VectorSpecies<Double> = DoubleVector.SPECIES_PREFERRED

class Conv1d(
    private val channel: Int,
    private val kernelSize: Int,
    override val activationFunction: (Double) -> Double,
) : Layer<IOType.IOType1d> {

    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun forward(
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
                )
            }
            for (outputTime in outputArray[outputChannel].indices) {
                outputArray[outputChannel][outputTime] = activationFunction(outputArray[outputChannel][outputTime])
            }
        }
    }

    override fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var beforeDeltaIndex = 0
        val beforeOutputArray = beforeOutput.asIOType1d().value

        // 出力信号の大きさ(どの層の組み合わせでも固定になる)
        val outputSize = beforeOutputArray.first().size - kernelSize + 1

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
    override fun backward(
        weight: Array<IOType>,
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d().value
        // 出力信号の大きさ(どの層の組み合わせでも固定になる)
        val outputSize = inputArray.first().size - kernelSize + 1

        for (inputChannel in weight.indices) {
            // 畳み込みの出力ニューロンを一列にした時のindexを表す
            var outputIndex = 0
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                for (kernelTime in weightArray[outputChannel].indices) {
                    var sum = 0.0
                    var outputTime = 0
                    while (outputTime < sp.loopBound(outputSize)) {
                        val i = DoubleVector.fromArray(sp, inputArray[inputChannel], kernelTime + outputTime)
                        val d = DoubleVector.fromArray(sp, delta, outputIndex + outputTime)
                        sum += i.mul(d).reduceLanes(VectorOperators.ADD)
                        outputTime += sp.length()
                    }
                    while (outputTime < outputSize) {
                        sum += inputArray[inputChannel][kernelTime + outputTime] * delta[outputIndex + outputTime]
                        outputTime++
                    }
                    weightArray[outputChannel][kernelTime] -= rate * sum
                }
                outputIndex += outputSize
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType1d().value.size) {
            IOType.IOType1d(Array(channel) { DoubleArray(kernelSize) { random.nextDouble(-1.0, 1.0) } })
        }

    override fun createOutput(input: IOType): IOType.IOType1d =
        IOType.IOType1d(Array(channel) { DoubleArray(input.asIOType1d().value.first().size - kernelSize + 1) })

    override fun createDelta(input: IOType): DoubleArray =
        DoubleArray(channel * (input.asIOType1d().value.first().size - kernelSize + 1))
}

inline fun DoubleArray.conv1d(
    kernel: DoubleArray,
    output: DoubleArray,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        var index = 0
        while (index < sp.loopBound(kernel.size)) {
            val i = DoubleVector.fromArray(sp, this, outputIndex + index)
            val k = DoubleVector.fromArray(sp, kernel, index)
            sum += i.mul(k).reduceLanes(VectorOperators.ADD)
            index += sp.length()
        }
        while (index < kernel.size) {
            sum += this[outputIndex + index] * kernel[index]
            index++
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
        var index = 0
        while (index < sp.loopBound(kernel.size)) {
            val i = DoubleVector.fromArray(sp, resizedInput, outputIndex + index)
            val k = DoubleVector.fromArray(sp, kernel, index)
            sum += i.mul(k).reduceLanes(VectorOperators.ADD)
            index += sp.length()
        }
        while (index < kernel.size) {
            sum += resizedInput[outputIndex + index] * kernel[index]
            index++
        }
        output[outputIndex] += sum
    }
}

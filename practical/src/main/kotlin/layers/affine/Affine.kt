@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.affine

import common.step
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import layers.IOType
import layers.Layer
import layers.conv.sp
import kotlin.random.Random

class Affine(
    private val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
) : Layer<IOType.IOType0d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType0d().value
        val outputArray = output.asIOType0d().value
        for (outputIndex in outputArray.indices) {
            var sum = 0.0
            for (inputIndex in inputArray.indices) {
                sum += inputArray[inputIndex] * weight[inputIndex].asIOType0d().value[outputIndex]
            }
            outputArray[outputIndex] = activationFunction(sum)
        }
    }

    /**
     * delta -> 計算結果を格納するdelta
     * output -> 自分の層の出力
     * afterDelta -> 後ろの層のdelta
     * afterWeight -> 自分と後ろの層の重み Array[前の層のニューロン][後ろの層のニューロン]
     */
    override inline fun calcDelta(
        beforeDelta: DoubleArray,
        beforeOutput: IOType,
        delta: DoubleArray,
        weight: Array<IOType>,
    ) {
        val beforeOutputArray = beforeOutput.asIOType0d().value
        for (inputIndex in beforeDelta.indices) {
            val weightArray = weight[inputIndex].asIOType0d().value
            var sum = 0.0
            var outputIndex = 0
            while (outputIndex < sp.loopBound(weightArray.size)) {
                val d = DoubleVector.fromArray(sp, delta, outputIndex)
                val we = DoubleVector.fromArray(sp, weightArray, outputIndex)
                sum += d.mul(we).reduceLanes(VectorOperators.ADD)
                outputIndex += sp.length()
            }
            while (outputIndex < weightArray.size) {
                sum += delta[outputIndex] * weightArray[outputIndex]
                outputIndex++
            }
            beforeDelta[inputIndex] = step(beforeOutputArray[inputIndex]) * sum
        }
    }

    override inline fun backward(
        weight: Array<IOType>,
        delta: DoubleArray,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType0d().value
        for (before in weight.indices) {
            for (after in weight[before].asIOType0d().value.indices) {
                weight[before].asIOType0d().value[after] -= rate * delta[after] * inputArray[before]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType0d().value.size) {
            IOType.IOType0d(DoubleArray(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
        }

    override fun createOutput(input: IOType): IOType.IOType0d = IOType.IOType0d(DoubleArray(numOfNeuron))
    override fun createDelta(input: IOType): DoubleArray = DoubleArray(numOfNeuron)
}

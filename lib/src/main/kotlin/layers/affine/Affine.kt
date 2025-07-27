@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.affine

import common.iotype.IOType
import common.iotype.IOType0d
import common.iotype.innerProduct
import common.step
import layers.Layer
import kotlin.random.Random

class Affine(
    private val numOfNeuron: Int,
    override val activationFunction: (Double) -> Double,
) : Layer<IOType0d> {
    override fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
    ) {
        val inputArray = input.asIOType0d()
        val outputArray = output.asIOType0d()
        for (outputIndex in outputArray.indices) {
            var sum = 0.0
            for (inputIndex in inputArray.indices) {
                sum += inputArray[inputIndex] * weight[inputIndex].asIOType0d()[outputIndex]
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
        beforeDelta: IOType,
        beforeOutput: IOType,
        delta: IOType,
        weight: Array<IOType>,
    ) {
        val beforeDeltaArray = beforeDelta.asIOType0d()
        val beforeOutputArray = beforeOutput.asIOType0d()
        val deltaArray = delta.asIOType0d()
        for (inputIndex in beforeDeltaArray.indices) {
            beforeDeltaArray[inputIndex] = step(beforeOutputArray[inputIndex]) *
                deltaArray.innerProduct(weight[inputIndex].asIOType0d(), 0)
        }
    }

    override inline fun backward(
        weight: Array<IOType>,
        delta: IOType,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType0d()
        val deltaArray = delta.asIOType0d()
        for (before in weight.indices) {
            val weightArray = weight[before].asIOType0d()
            for (after in weightArray.indices) {
                weightArray[after] -= rate * deltaArray[after] * inputArray[before]
            }
        }
    }

    override fun createWeight(input: IOType, random: Random): Array<IOType> =
        Array(input.asIOType0d().size) {
            IOType0d(MutableList(numOfNeuron) { random.nextDouble(-1.0, 1.0) })
        }

    override fun createOutput(input: IOType): IOType0d = IOType0d(MutableList(numOfNeuron) { 0.0 })
    override fun createDelta(input: IOType): IOType0d = IOType0d(MutableList(numOfNeuron) { 0.0 })
}

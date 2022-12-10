@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer0d

import common.step
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import layers.IOType
import layers.LayerType
import layers.layer1d.sp

object Affine : LayerType {
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double,
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
        for (i in beforeDelta.indices) {
            val weightArray = weight[i].asIOType0d().value
            var sum = 0.0
            for (w in 0 until sp.loopBound(weightArray.size) step sp.length()) {
                val d = DoubleVector.fromArray(sp, delta, w)
                val we = DoubleVector.fromArray(sp, weightArray, w)
                sum += d.mul(we).reduceLanes(VectorOperators.ADD)
            }
            beforeDelta[i] = step(beforeOutputArray[i]) * sum
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
}

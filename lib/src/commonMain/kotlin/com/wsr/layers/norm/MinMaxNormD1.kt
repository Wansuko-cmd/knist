package com.wsr.layers.norm

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.d1.minus
import com.wsr.d1.plus
import com.wsr.d1.times
import com.wsr.layers.Layer
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private var alpha: IOType.D1,
) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        val f1 = input
        val f2 = input
        val f3 = input

        val min = f2.map { it.value.min() }
        val f4 = min
        val f5 = min

        val max = f3.map { it.value.max() }
        val f6 = max

        val f7 = f1.mapIndexed { i, f1 -> IOType.d1(f1.shape) { x -> f1[x] - f4[i] } }
        val f8 = List(f6.size) { f6[it] - f5[it] }
        val f9 = List(f8.size) { 1 / f8[it] }

        val f10 = f7.mapIndexed { i, f7 -> IOType.d1(f7.shape) { f7[it] * f9[i] } }

        return f10.map { it * alpha }
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val f1 = input
        val f2 = input
        val f3 = input

        val min = f2.map { it.value.min() }
        val f4 = min
        val f5 = min

        val max = f3.map { it.value.max() }
        val f6 = max

        val f7 = f1.mapIndexed { i, f1 -> IOType.d1(f1.shape) { x -> f1[x] - f4[i] } }
        val f8 = List(f6.size) { f6[it] - f5[it] }
        val f9 = List(f8.size) { 1 / f8[it] }

        val f10 = f7.mapIndexed { i, f7 -> IOType.d1(f7.shape) { f7[it] * f9[i] } }

        val delta = calcDelta(f10.map { it * alpha })

        val d10 = delta.map { it * alpha }
        alpha -= rate * IOType.d1(alpha.shape) { x -> (0 until f10.size).sumOf { f10[it][x] * delta[it][x] } }

        val d9 = List(delta.size) { IOType.d1(f7[it].shape) { x -> f7[it][x] * d10[it][x] } }
        val d7 = List(delta.size) { IOType.d1(f7[it].shape) { x -> f9[it] * d10[it][x] } }

        val d8 = List(delta.size) { -1.0 / f8[it].pow(2) * d9[it].value.sum() }

        val d6 = List(delta.size) { -1.0 * f5[it] * d8[it] }
        val d5 = List(delta.size) { f6[it] * d8[it] }

        val d4 = List(input.size) { f1[it] * d7[it] }
        val d1 = List(input.size) { -1.0 * f4[it] * d7[it] }

        val d2 = List(input.size) {
            IOType.d1(outputSize) { x ->
                if (f2[it][x] == min[it]) d4[it][x] + d5[it] else 0.0
            }
        }

        val d3 = List(input.size) {
            IOType.d1(outputSize) { x ->
                if (f3[it][x] == max[it]) d6[it] else 0.0
            }
        }

        return List(input.size) { d1[it] + d2[it] + d3[it] }
    }

    private fun IOType.D1.pow() = IOType.d1(shape) { this[it] * this[it] }

    private operator fun Double.div(other: IOType.D1) = IOType.d1(other.shape) { if (other[it] != 0.0) this / other[it] else 0.0001 }

    private operator fun IOType.D1.times(other: IOType.D1) = IOType.d1(shape) { this[it] * other[it] }

    private operator fun IOType.D1.div(other: IOType.D1) = IOType.d1(shape) { this[it] / other[it] }
}

fun <T : IOType> NetworkBuilder.D1<T>.minMaxNorm() = addLayer(
    layer = MinMaxNormD1(
        outputSize = inputSize,
        rate = rate,
        alpha = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)

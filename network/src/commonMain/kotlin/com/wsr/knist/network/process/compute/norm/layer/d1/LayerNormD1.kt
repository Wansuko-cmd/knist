package com.wsr.knist.network.process.compute.norm.layer.d1

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD1 internal constructor(override val inputI: Int, private val e: Float, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI: Int get() = inputI
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return numerator / denominator
    }

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        val output = numerator / denominator
        val delta = calcDelta(output)

        val dNumerator = delta / denominator

        val dx1 = dNumerator
        val dx2 = -dNumerator.average()
        val dx3 = (-output * dNumerator).average() * output

        return dx1 + dx2 + dx3
    }
}

fun GraphBuilder.Node.D1.layerNorm(e: Float = 1e-6f, id: String = Uuid.random().toString()) = addCompute(
    compute = LayerNormD1(inputI = inputI, e = e, id = id),
)

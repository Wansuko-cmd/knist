package com.wsr.knist.network.process.compute.norm.batch.d1

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class BatchNormD1(override val inputI: Int, private val e: Float, private val momentum: Float, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI = inputI
    private var rm: IOType.D1.Global = IOType.d1(i = inputI)
    private var rv: IOType.D1.Global = IOType.d1(i = inputI) { 1f }

    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> = (input - rm) / rv.sqrt(e = e)

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val average = input.batchAverage()
        val numerator = input - average

        val variance = numerator.pow(n = 2).batchAverage()
        val denominator = variance.sqrt(e = e)

        val output = numerator / denominator
        val delta = calcDelta(output)

        val dNumerator = delta / denominator

        val dx1 = dNumerator
        val dx2 = -dNumerator.batchAverage()
        val dx3 = (-output * dNumerator).batchAverage() * output

        rm = (momentum * rm + (1 - momentum) * average).toGlobal()
        rv = (momentum * rv + (1 - momentum) * variance).toGlobal()

        return dx1 + dx2 + dx3
    }
}

fun GraphBuilder.Node.D1.batchNorm(e: Float = 1e-6f, momentum: Float = 0.9f, id: String = Uuid.random().toString()) = addCompute(
    compute = BatchNormD1(inputI = inputI, e = e, momentum = momentum, id = id),
)

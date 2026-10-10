package com.wsr.knist.network.process.compute.norm.batch.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
class BatchNormD3(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private val e: Float,
    private val momentum: Float,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val outputI = inputI
    override val outputJ = inputJ
    override val outputK = inputK
    private var rm: IOType.D3.Global = IOType.d3(i = inputI, j = inputJ, k = inputK)
    private var rv: IOType.D3.Global = IOType.d3(i = inputI, j = inputJ, k = inputK) { _, _, _ -> 1f }

    override fun IOScope.expect(
        input: Batch<IOType.D3>,
        env: GraphEnv,
    ): Batch<IOType.D3> {
        return (input - rm) / rv.sqrt(e = e)
    }

    override fun IOScope.train(
        input: Batch<IOType.D3>,
        env: GraphEnv,
        calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
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

        rm = (momentum * rm  + (1 - momentum) * average).toGlobal()
        rv = (momentum * rv  + (1 - momentum) * variance).toGlobal()

        return dx1 + dx2 + dx3
    }
}

fun GraphBuilder.Node.D3.batchNorm(e: Float = 1e-6f, momentum: Float = 0.9f, id: String = Uuid.random().toString()) = addCompute(
    compute = BatchNormD3(inputI = inputI, inputJ = inputJ, inputK = inputK, e = e, momentum = momentum, id = id),
)

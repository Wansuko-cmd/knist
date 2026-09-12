package com.wsr.knist.network.process.compute.position

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class PositionEmbeddingD2 internal constructor(
    override val inputI: Int,
    override val inputJ: Int,
    private var optimizer: Optimizer.D2,
    private var weight: IOType.D2.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input + weight

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input + weight
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight = weight, dw = delta).toGlobal()
        return delta
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d2(i = inputI, j = inputJ)
    }
}

fun GraphBuilder.Node.D2.positionEmbedding(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = this.initializer, id: String = Uuid.random().toString()) = addCompute(
    compute = PositionEmbeddingD2(
        inputI = inputI,
        inputJ = inputJ,
        optimizer = optimizer.d2(inputI, inputJ),
        weight = initializer.d2(
            input = listOf(inputI, inputJ),
            output = listOf(inputI, inputJ),
            i = inputI,
            j = inputJ,
        ),
        id = id,
    ),
)

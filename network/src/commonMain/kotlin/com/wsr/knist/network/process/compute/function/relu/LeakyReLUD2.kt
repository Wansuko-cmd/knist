package com.wsr.knist.network.process.compute.function.relu

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD2 internal constructor(override val inputI: Int, override val inputJ: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val mask = input gt 0f
        return input.where(condition = mask, onFalse = 0.01f * input)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val mask = input gt 0f
        val output = input.where(condition = mask, onFalse = 0.01f * input)
        val delta = calcDelta(output)
        return delta.where(condition = mask, onFalse = 0.01f * delta)
    }
}

fun GraphBuilder.Node.D2.leakyReLU(id: String = Uuid.random().toString()) = addCompute(
    LeakyReLUD2(inputI = inputI, inputJ = inputJ, id = id),
)

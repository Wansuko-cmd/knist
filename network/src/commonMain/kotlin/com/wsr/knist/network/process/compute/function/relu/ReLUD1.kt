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
class ReLUD1 internal constructor(override val inputI: Int, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI: Int get() = inputI
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> {
        val mask = input gt 0f
        return input.where(condition = mask, onFalse = 0f)
    }

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val mask = input gt 0f
        val output = input.where(condition = mask, onFalse = 0f)
        val delta = calcDelta(output)
        return delta.where(condition = mask, onFalse = 0f)
    }
}

fun GraphBuilder.Node.D1.reLU(id: String = Uuid.random().toString()) = addCompute(ReLUD1(inputI = inputI, id = id))

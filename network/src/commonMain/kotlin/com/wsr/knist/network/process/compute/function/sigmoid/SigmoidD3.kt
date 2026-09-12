package com.wsr.knist.network.process.compute.function.sigmoid

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
class SigmoidD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.sigmoid()

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val output = input.sigmoid()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun GraphBuilder.Node.D3.sigmoid(id: String = Uuid.random().toString()) = addCompute(
    compute = SigmoidD3(inputI = inputI, inputJ = inputJ, inputK = inputK, id = id),
)

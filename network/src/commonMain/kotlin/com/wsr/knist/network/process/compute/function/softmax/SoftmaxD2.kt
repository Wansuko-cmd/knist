package com.wsr.knist.network.process.compute.function.softmax

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
class SoftmaxD2 internal constructor(override val inputI: Int, override val inputJ: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.softmax()

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input.softmax()
        val delta = calcDelta(output)
        val sum = (delta * output).sum()
        return output * (delta - sum)
    }
}

fun GraphBuilder.Node.D2.softmax(id: String = Uuid.random().toString()) = addCompute(
    compute = SoftmaxD2(inputI = inputI, inputJ = inputJ, id = id),
)

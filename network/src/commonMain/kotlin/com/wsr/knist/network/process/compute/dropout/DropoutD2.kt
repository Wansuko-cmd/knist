package com.wsr.knist.network.process.compute.dropout

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.random.Random
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class DropoutD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val ratio: Float, private val seed: Int? = null, override val id: String = Uuid.random().toString()) :
    Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val uniform = Batch.random(
            size = input.size,
            i = outputI,
            j = outputJ,
            from = 0f,
            until = 1f,
            random = random,
        )
        val mask = uniform.where(condition = uniform lt ratio, onTrue = q, onFalse = 0f)
        val output = input * mask
        val delta = calcDelta(output)
        return delta * mask
    }
}

fun GraphBuilder.Node.D2.dropout(ratio: Float, seed: Int? = null, id: String = Uuid.random().toString()) = addCompute(
    compute =
        DropoutD2(
            inputI = inputI,
            inputJ = inputJ,
            ratio = ratio,
            seed = seed,
            id = id,
        ),
)

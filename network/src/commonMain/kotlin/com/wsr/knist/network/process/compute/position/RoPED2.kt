package com.wsr.knist.network.process.compute.position

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class RoPED2 internal constructor(override val inputI: Int, override val inputJ: Int, private val waveLength: Float, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val theta by lazy {
        FloatArray(outputJ / 2) { i -> 1f / waveLength.pow(2f * i / outputJ) }
    }

    private val cosCache by lazy {
        IOType.d2(outputI, outputJ / 2) { x, y -> cos(x * theta[y]) }
    }

    private val sinCache by lazy {
        IOType.d2(outputI, outputJ / 2) { x, y -> sin(x * theta[y]) }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = forward(input)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        return forward(delta)
    }

    private fun IOScope.forward(input: Batch<IOType.D2>): Batch<IOType.D2> {
        val even = input.slice(0 until input.shape[1] step 2, axis = 1)
        val odd = input.slice(1 until input.shape[1] step 2, axis = 1)

        val resEven = even * cosCache - odd * sinCache
        val resOdd = even * sinCache + odd * cosCache

        return resEven.interleave(resOdd, axis = 1)
    }
}

@Deprecated("実装ミス。Attentionの中に組み込む必要がある")
fun GraphBuilder.Node.D2.roPE(waveLength: Float = 10000f, id: String = Uuid.random().toString()) = addCompute(
    compute = RoPED2(
        inputI = inputI,
        inputJ = inputJ,
        waveLength = waveLength,
        id = id,
    ),
)

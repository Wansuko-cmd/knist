package com.wsr.knist.network.process.compute.debug

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK

    @Transient
    var onInput: (Batch<IOType.D3>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D3>) -> Unit = {}

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.also {
        onInput(it)
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun GraphBuilder.Node.D3.debug(onInput: (Batch<IOType.D3>) -> Unit = {}, onDelta: (Batch<IOType.D3>) -> Unit = {}, id: String = Uuid.random().toString()) = addCompute(
    compute = DebugD3(
        inputI = inputI,
        inputJ = inputJ,
        inputK = inputK,
        id = id,
    ).apply {
        this.onInput = onInput
        this.onDelta = onDelta
    },
)

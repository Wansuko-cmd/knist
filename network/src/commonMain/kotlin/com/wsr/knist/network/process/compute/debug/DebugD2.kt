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
class DebugD2 internal constructor(override val inputI: Int, override val inputJ: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ

    @Transient
    var onInput: (Batch<IOType.D2>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D2>) -> Unit = {}

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.also {
        onInput(it)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun GraphBuilder.Node.D2.debug(onInput: (Batch<IOType.D2>) -> Unit = {}, onDelta: (Batch<IOType.D2>) -> Unit = {}, id: String = Uuid.random().toString()) = addCompute(
    compute = DebugD2(
        inputI = inputI,
        inputJ = inputJ,
        id = id,
    ).apply {
        this.onInput = onInput
        this.onDelta = onDelta
    },
)

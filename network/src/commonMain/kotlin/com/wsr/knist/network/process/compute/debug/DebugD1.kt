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
class DebugD1 internal constructor(override val inputI: Int, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI: Int get() = inputI

    @Transient
    var onInput: (Batch<IOType.D1>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D1>) -> Unit = {}

    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> = input.also {
        onInput(it)
    }

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun GraphBuilder.Node.D1.debug(onInput: (Batch<IOType.D1>) -> Unit = {}, onDelta: (Batch<IOType.D1>) -> Unit = {}, id: String = Uuid.random().toString()) = addCompute(
    compute = DebugD1(inputI = inputI, id = id)
        .apply {
            this.onInput = onInput
            this.onDelta = onDelta
        },
)

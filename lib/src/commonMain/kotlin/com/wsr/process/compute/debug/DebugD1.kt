package com.wsr.process.compute.debug

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD1 internal constructor(override val outputSize: Int) : Compute.D1() {
    @Transient
    var onInput: (Batch<IOType.D1>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D1>) -> Unit = {}

    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.also { onInput(it) }

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D1<T>.debug(onInput: (Batch<IOType.D1>) -> Unit = {}, onDelta: (Batch<IOType.D1>) -> Unit = {}) =
    addProcess(
        process = DebugD1(outputSize = inputSize)
            .apply {
                this.onInput = onInput
                this.onDelta = onDelta
            },
    )

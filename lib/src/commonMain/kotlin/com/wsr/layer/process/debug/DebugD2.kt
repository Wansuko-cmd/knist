package com.wsr.layer.process.debug

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    @Transient
    var onInput: (Batch<IOType.D2>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D2>) -> Unit = {}

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.also { onInput(it) }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D2<T>.debug(onInput: (Batch<IOType.D2>) -> Unit = {}, onDelta: (Batch<IOType.D2>) -> Unit = {}) =
    addProcess(
        process = DebugD2(
            outputX = inputX,
            outputY = inputY,
        ).apply {
            this.onInput = onInput
            this.onDelta = onDelta
        },
    )

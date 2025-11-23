package com.wsr.layer.process.debug

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    @Transient
    var onInput: (Batch<IOType.D3>) -> Unit = {}

    @Transient
    var onDelta: (Batch<IOType.D3>) -> Unit = {}

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.also { onInput(it) }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D3<T>.debug(onInput: (Batch<IOType.D3>) -> Unit = {}, onDelta: (Batch<IOType.D3>) -> Unit = {}) =
    addProcess(
        process = DebugD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
        ).apply {
            this.onInput = onInput
            this.onDelta = onDelta
        },
    )

package com.wsr.layer.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    @Transient
    var onInput: (List<IOType.D3>) -> Unit = {}

    @Transient
    var onDelta: (List<IOType.D3>) -> Unit = {}

    override fun expect(input: List<IOType.D3>, context: Context): List<IOType.D3> = input.also { onInput(it) }

    override fun train(
        input: List<IOType.D3>,
        context: Context,
        calcDelta: (List<IOType.D3>) -> List<IOType.D3>,
    ): List<IOType.D3> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D3<T>.debug(onInput: (List<IOType.D3>) -> Unit = {}, onDelta: (List<IOType.D3>) -> Unit = {}) =
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

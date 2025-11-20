package com.wsr.layer.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    @Transient
    var onInput: (List<IOType.D2>) -> Unit = {}

    @Transient
    var onDelta: (List<IOType.D2>) -> Unit = {}

    override fun expect(input: List<IOType.D2>, context: Context): List<IOType.D2> = input.also { onInput(it) }

    override fun train(
        input: List<IOType.D2>,
        context: Context,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D2<T>.debug(onInput: (List<IOType.D2>) -> Unit = {}, onDelta: (List<IOType.D2>) -> Unit = {}) =
    addProcess(
        process = DebugD2(
            outputX = inputX,
            outputY = inputY,
        ).apply {
            this.onInput = onInput
            this.onDelta = onDelta
        },
    )

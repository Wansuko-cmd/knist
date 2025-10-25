package com.wsr.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DebugD1 internal constructor(override val outputSize: Int) : Process.D1() {
    @Transient
    var onInput: (List<IOType.D1>) -> Unit = {}

    @Transient
    var onDelta: (List<IOType.D1>) -> Unit = {}

    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.also { onInput(it) }

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * ※Json化するとラムダ式はリセットされる
 */
fun <T> NetworkBuilder.D1<T>.debug(onInput: (List<IOType.D1>) -> Unit = {}, onDelta: (List<IOType.D1>) -> Unit = {}) =
    addProcess(
        process = DebugD1(outputSize = inputSize)
            .apply {
                this.onInput = onInput
                this.onDelta = onDelta
            },
    )

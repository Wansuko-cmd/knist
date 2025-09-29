package com.wsr.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process

class DebugD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val onInput: (List<IOType.D2>) -> Unit,
    private val onDelta: (List<IOType.D2>) -> Unit,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.also { onInput(it) }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * Json時には除かれる(lambdaは変換できないため)
 */
fun <T : IOType> NetworkBuilder.D2<T>.debug(
    onInput: (List<IOType.D2>) -> Unit = {},
    onDelta: (List<IOType.D2>) -> Unit = {},
) = addProcess(
    DebugD2(
        outputX = inputX,
        outputY = inputY,
        onInput = onInput,
        onDelta = onDelta,
    ),
)

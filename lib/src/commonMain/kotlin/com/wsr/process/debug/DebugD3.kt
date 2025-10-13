package com.wsr.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process

class DebugD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val onInput: (List<IOType.D3>) -> Unit,
    private val onDelta: (List<IOType.D3>) -> Unit,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input.also { onInput(it) }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * Json時には除かれる(lambdaは変換できないため)
 */
fun <T : IOType> NetworkBuilder.D3<T>.debug(
    onInput: (List<IOType.D3>) -> Unit = {},
    onDelta: (List<IOType.D3>) -> Unit = {},
) = addProcess(
    DebugD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        onInput = onInput,
        onDelta = onDelta,
    ),
)

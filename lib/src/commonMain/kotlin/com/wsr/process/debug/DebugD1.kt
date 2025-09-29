package com.wsr.process.debug

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process

class DebugD1 internal constructor(
    override val outputSize: Int,
    private val onInput: (List<IOType.D1>) -> Unit,
    private val onDelta: (List<IOType.D1>) -> Unit,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.also { onInput(it) }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val input = input.also { onInput(it) }
        val delta = calcDelta(input).also { onDelta(it) }
        return delta
    }
}

/**
 * Json時には除かれる(lambdaは変換できないため)
 */
fun <T : IOType> NetworkBuilder.D1<T>.debug(
    onInput: (List<IOType.D1>) -> Unit = {},
    onDelta: (List<IOType.D1>) -> Unit = {},
) = addProcess(
    DebugD1(
        outputSize = inputSize,
        onInput = onInput,
        onDelta = onDelta,
    ),
)

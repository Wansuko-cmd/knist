package com.wsr.layers.debug

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer

class DebugD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val onInput: (IOType.D2) -> Unit,
    private val onDelta: (IOType.D2) -> Unit,
) : Layer.D2() {
    override fun expect(input: IOType.D2): IOType.D2 = input.also(onInput)

    override fun train(
        input: IOType.D2,
        calcDelta: (IOType.D2) -> IOType.D2,
    ): IOType.D2 {
        val input = input.also(onInput)
        return calcDelta(input).also(onDelta)
    }
}

/**
 * Json時には除かれる(lambdaは変換できないため)
 */
fun <T : IOType> NetworkBuilder.D2<T>.debug(
    onInput: (IOType.D2) -> Unit = {},
    onDelta: (IOType.D2) -> Unit = {},
) = addLayer(
    DebugD2(
        outputX = inputX,
        outputY = inputY,
        onInput = onInput,
        onDelta = onDelta,
    ),
)

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
    override fun expectD2(input: List<IOType.D2>): List<IOType.D2> {
        TODO("Not yet implemented")
    }

    override fun trainD2(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        TODO("Not yet implemented")
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

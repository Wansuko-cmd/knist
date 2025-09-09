package com.wsr.layers.debug

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer

class DebugD1 internal constructor(
    override val outputSize: Int,
    private val onInput: (IOType.D1) -> Unit,
    private val onDelta: (IOType.D1) -> Unit,
) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        TODO("Not yet implemented")
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        TODO("Not yet implemented")
    }
}

/**
 * Json時には除かれる(lambdaは変換できないため)
 */
fun <T : IOType> NetworkBuilder.D1<T>.debug(
    onInput: (IOType.D1) -> Unit = {},
    onDelta: (IOType.D1) -> Unit = {},
) = addLayer(
    DebugD1(
        outputSize = inputSize,
        onInput = onInput,
        onDelta = onDelta,
    ),
)

package com.wsr.layers.function.linear

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class LinearD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Layer.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> = calcDelta(input)
}

fun <T : IOType> NetworkBuilder.D2<T>.linear() = addLayer(
    layer= LinearD2(outputX = inputX, outputY = inputY),
)

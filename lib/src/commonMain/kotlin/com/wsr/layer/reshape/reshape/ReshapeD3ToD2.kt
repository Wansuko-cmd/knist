package com.wsr.layer.reshape.reshape

import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D2
import com.wsr.batch.Batch
import com.wsr.batch.reshape.reshape.reshapeToD2
import com.wsr.batch.reshape.reshape.reshapeToD3
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD2(override val outputX: Int, override val outputY: Int) : Reshape.D3ToD2() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D2> =
        input.reshapeToD2(i = outputX, j = outputY)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D3> {
        val output = input.reshapeToD2(i = outputX, j = outputY)
        val delta = calcDelta(output)
        return delta.reshapeToD3(input.shape)
    }
}

fun <T> NetworkBuilder.D3<T>.reshapeToD2(outputX: Int, outputY: Int): D2<T> {
    check(inputX * inputY * inputZ == outputX * outputY) {
        """
            invalid parameter.
            inputX: $inputX
            inputY: $inputY
            inputZ: $inputZ
            outputX: $outputX
            outputY: $outputY
        """.trimIndent()
    }
    return addReshape(
        reshape = ReshapeD3ToD2(
            outputX = outputX,
            outputY = outputY,
        ),
    )
}

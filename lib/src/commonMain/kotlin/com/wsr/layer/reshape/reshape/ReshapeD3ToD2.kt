package com.wsr.layer.reshape.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D2
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD2(override val outputX: Int, override val outputY: Int) : Reshape.D3ToD2() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D2> = input.toList().map {
        IOType.d2(shape = listOf(outputX, outputY), value = it.value)
    }.toBatch()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D3> {
        val input = input.toList()
        val output = input.toList().map {
            IOType.d2(shape = listOf(outputX, outputY), value = it.value)
        }
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d3(shape = input[i].shape, value = delta[i].value)
        }.toBatch()
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

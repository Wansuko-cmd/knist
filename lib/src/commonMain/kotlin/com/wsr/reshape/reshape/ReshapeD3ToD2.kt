package com.wsr.reshape.reshape

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D2
import com.wsr.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD2(override val outputX: Int, override val outputY: Int) : Reshape.D3ToD2() {
    override fun expect(input: List<IOType.D3>): List<IOType.D2> = input.map {
        IOType.d2(shape = listOf(outputX, outputY), value = it.value)
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D3> {
        val output = input.map {
            IOType.d2(shape = listOf(outputX, outputY), value = it.value)
        }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(shape = input[i].shape, value = delta[i].value)
        }
    }
}

fun <T : IOType> NetworkBuilder.D3<T>.reshapeToD2(outputX: Int, outputY: Int): D2<T> {
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

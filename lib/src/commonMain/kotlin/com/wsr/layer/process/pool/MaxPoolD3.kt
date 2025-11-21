package com.wsr.layer.process.pool

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class MaxPoolD3 internal constructor(val poolSize: Int, val channel: Int, val inputX: Int, val inputY: Int) :
    Process.D3() {
    override val outputX: Int = channel
    override val outputY: Int = inputX / poolSize
    override val outputZ: Int = inputY / poolSize

    init {
        check(inputX % poolSize == 0 && inputY % poolSize == 0) {
            """
            invalid parameter.
            inputX: $inputX
            inputY: $inputY
            poolSize: $poolSize
            outputX: ${inputX / poolSize.toFloat()}
            outputY: ${inputY / poolSize.toFloat()}
            """.trimIndent()
        }
    }

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.toList().map(::forward).toBatch()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val input = input.toList()
        val output = input.map(::forward)
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { index ->
            IOType.d3(i = channel, j = inputX, k = inputY) { c, x, y ->
                val xo = x / poolSize
                val yo = y / poolSize
                if (input[index][c, x, y] == output[index][c, xo, yo]) delta[index][c, xo, yo] else 0f
            }
        }.toBatch()
    }

    private fun forward(input: IOType.D3): IOType.D3 = IOType.d3(outputX, outputY, outputZ) { x, y, z ->
        var max = input[x, y, z]
        for (i in 0 until poolSize) {
            for (j in 0 until poolSize) {
                max = maxOf(max, input[x, y + i, z + j])
            }
        }
        max
    }
}

fun <T> NetworkBuilder.D3<T>.maxPool(size: Int) = addProcess(
    process = MaxPoolD3(
        poolSize = size,
        channel = inputX,
        inputX = inputY,
        inputY = inputZ,
    ),
)

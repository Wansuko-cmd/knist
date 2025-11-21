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
class MaxPoolD2 internal constructor(val poolSize: Int, val channel: Int, val inputSize: Int) : Process.D2() {
    override val outputX: Int = channel
    override val outputY: Int = inputSize / poolSize

    init {
        check(inputSize % poolSize == 0) {
            """
            invalid parameter.
            inputSize: $inputSize
            poolSize: $poolSize
            output: ${inputSize / poolSize.toFloat()}
            """.trimIndent()
        }
    }

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> =
        input.toList().map(::forward).toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val output = input.map(::forward)
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { index ->
            IOType.d2(channel, inputSize) { c, i ->
                val o = i / poolSize
                if (input[index][c, i] == output[index][c, o]) delta[index][c, o] else 0f
            }
        }.toBatch()
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.d2(outputX, outputY) { x, y ->
        var max = input[x, y]
        for (i in 1 until poolSize) {
            max = maxOf(max, input[x, y + i])
        }
        max
    }
}

fun <T> NetworkBuilder.D2<T>.maxPool(size: Int) = addProcess(
    process =
    MaxPoolD2(
        poolSize = size,
        channel = inputX,
        inputSize = inputY,
    ),
)

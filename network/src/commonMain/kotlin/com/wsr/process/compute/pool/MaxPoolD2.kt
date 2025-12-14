package com.wsr.process.compute.pool

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class MaxPoolD2 internal constructor(val poolSize: Int, val channel: Int, val inputSize: Int) : Compute.D2() {
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

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.map(::forward)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return Batch(input.size) { index ->
            val input = input[index]
            val output = output[index]
            val delta = delta[index]
            IOType.d2(channel, inputSize) { c, i ->
                val o = i / poolSize
                if (input[c, i] == output[c, o]) delta[c, o] else 0f
            }
        }
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

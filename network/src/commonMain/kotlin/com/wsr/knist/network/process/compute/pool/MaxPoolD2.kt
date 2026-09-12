package com.wsr.knist.network.process.compute.pool

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class MaxPoolD2 internal constructor(val poolSize: Int, val channel: Int, val inputSize: Int, val padding: Int, val stride: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val inputI: Int = channel
    override val inputJ: Int = inputSize

    override val outputI: Int = channel
    override val outputJ: Int = (inputSize + 2 * padding - poolSize) / stride + 1

    init {
        check((inputSize + 2 * padding - poolSize) % stride == 0) {
            """
            invalid parameter.
            inputSize: $inputSize
            poolSize: $poolSize
            stride: $stride
            padding: $padding
            output: ${(inputSize + 2 * padding - poolSize) / stride.toFloat() + 1}
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.unfold(
        window = poolSize,
        stride = stride,
        dilation = 1,
        padding = padding,
    )
        .max(axis = 2)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val unfold = input.unfold(window = poolSize, stride = stride, dilation = 1, padding = padding)
        val output = unfold.max(axis = 2)
        val delta = calcDelta(output)
        return where(
            condition = unfold eq output.broadcastToD3(axis = 2, size = poolSize),
            onTrue = delta.broadcastToD3(axis = 2, size = poolSize),
            onFalse = 0f,
        ).fold(stride = stride, dilation = 1, padding = padding)
    }
}

fun GraphBuilder.Node.D2.maxPool(size: Int, stride: Int = size, padding: Int = 0, id: String = Uuid.random().toString()) = addCompute(
    compute = MaxPoolD2(
        poolSize = size,
        channel = inputI,
        inputSize = inputJ,
        padding = padding,
        stride = stride,
        id = id,
    ),
)

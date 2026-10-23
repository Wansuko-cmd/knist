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

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input
        .let { if (padding == 0) it else it.padding(axis = 1, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY) }
        .unfold(window = poolSize, stride = stride, dilation = 1, padding = 0)
        .max(axis = 2)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val unfold = input
            .let { if (padding == 0) it else it.padding(axis = 1, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY) }
            .unfold(window = poolSize, stride = stride, dilation = 1, padding = 0)
        val output = unfold.max(axis = 2)
        val delta = calcDelta(output)

        // 同値が複数ある場合は勾配を等分
        val mask = unfold eq output.broadcastToD3(axis = 2, size = poolSize)
        val count = mask.sum(axis = 2)
        val dx = (delta / count).broadcastToD3(axis = 2, size = poolSize)
            .where(condition = mask, onFalse = 0f)
            .fold(stride = stride, dilation = 1, padding = 0)
            .let { if (padding == 0) it else it.slice(indices = padding until padding + inputSize, axis = 1) }
        return dx
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

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
class MaxPoolD3 internal constructor(val poolSize: Int, val channel: Int, val height: Int, val width: Int, val padding: Int, val stride: Int, override val id: String = Uuid.random().toString()) :
    Compute.D3() {
    override val inputI: Int = channel
    override val inputJ: Int = height
    override val inputK: Int = width

    override val outputI: Int = channel
    override val outputJ: Int = (height + 2 * padding - poolSize) / stride + 1
    override val outputK: Int = (width + 2 * padding - poolSize) / stride + 1

    init {
        require(
            (height + 2 * padding - poolSize) % stride == 0 &&
                (width + 2 * padding - poolSize) % stride == 0,
        ) {
            """
            invalid parameter.
            height: $height
            width: $width
            poolSize: $poolSize
            stride: $stride
            padding: $padding
            outputJ: ${(height + 2 * padding - poolSize) / stride.toFloat() + 1}
            outputK: ${(width + 2 * padding - poolSize) / stride.toFloat() + 1}
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input
        .let {
            if (padding == 0) {
                it
            } else {
                it
                    .padding(axis = 1, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY)
                    .padding(axis = 2, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY)
            }
        }
        .unfold(window = poolSize, stride = stride, dilation = 1, padding = 0)
        .max(axis = 3)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val unfold = input
            .let {
                if (padding == 0) {
                    it
                } else {
                    it
                        .padding(axis = 1, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY)
                        .padding(axis = 2, left = padding, right = padding, fill = Float.NEGATIVE_INFINITY)
                }
            }
            .unfold(window = poolSize, stride = stride, dilation = 1, padding = 0)
        val output = unfold.max(axis = 3)
        val delta = calcDelta(output)

        // 同値が複数ある場合は勾配を等分
        val windowSize = poolSize * poolSize
        val mask = unfold.eq(output.broadcastToD4(axis = 3, size = windowSize), absoluteTolerance = 0f, relativeTolerance = 0f)
        val count = mask.sum(axis = 3)
        val dx = (delta / count).broadcastToD4(axis = 3, size = windowSize)
            .where(condition = mask, onFalse = 0f)
            .fold(stride = stride, dilation = 1, padding = 0)
            .let {
                if (padding == 0) {
                    it
                } else {
                    it
                        .slice(indices = padding until padding + height, axis = 1)
                        .slice(indices = padding until padding + width, axis = 2)
                }
            }
        return dx
    }
}

fun GraphBuilder.Node.D3.maxPool(size: Int, stride: Int = size, padding: Int = 0, id: String = Uuid.random().toString()) = addCompute(
    compute = MaxPoolD3(
        poolSize = size,
        channel = inputI,
        height = inputJ,
        width = inputK,
        padding = padding,
        stride = stride,
        id = id,
    ),
)

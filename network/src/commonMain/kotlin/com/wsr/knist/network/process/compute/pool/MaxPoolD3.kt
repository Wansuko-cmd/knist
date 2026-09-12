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
        check(
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

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.unfold(
        window = poolSize,
        stride = stride,
        dilation = 1,
        padding = padding,
    )
        .max(axis = 3)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val unfold = input.unfold(window = poolSize, stride = stride, dilation = 1, padding = padding)
        val output = unfold.max(axis = 3)
        val delta = calcDelta(output)
        val windowSize = poolSize * poolSize
        return where(
            condition = unfold eq output.broadcastToD4(axis = 3, size = windowSize),
            onTrue = delta.broadcastToD4(axis = 3, size = windowSize),
            onFalse = 0f,
        ).fold(stride = stride, dilation = 1, padding = padding)
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

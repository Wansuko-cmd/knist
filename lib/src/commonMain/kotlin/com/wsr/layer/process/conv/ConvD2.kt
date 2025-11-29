package com.wsr.layer.process.conv

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class ConvD2 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val padding: Int,
    private val inputX: Int,
    private val inputY: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D4,
) : Process.D3() {
    override val outputX: Int = filter
    override val outputY: Int = (inputX - kernel + 2 * padding) / stride + 1
    override val outputZ: Int = (inputY - kernel + 2 * padding) / stride + 1
    override fun expect(
        input: Batch<IOType.D3>,
        context: Context,
    ): Batch<IOType.D3> {
        TODO("Not yet implemented")
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        TODO("Not yet implemented")
    }
}

fun <T> NetworkBuilder.D3<T>.convD2(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process =
        ConvD2(
            filter = filter,
            channel = inputX,
            kernel = kernel,
            stride = stride,
            padding = padding,
            inputX = inputY,
            inputY = inputZ,
            optimizer = optimizer.d3(filter, inputX, kernel),
            weight = initializer.d4(
                input = listOf(inputX, kernel, kernel),
                output = listOf(filter, kernel, kernel),
                i = filter,
                j = inputX,
                k = kernel,
                l = kernel,
            ),
        ),
)

package com.wsr.layer.process.position

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class PositionEmbeddingD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> =
        (input.toList() + weight).toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val output = input + weight
        val delta = calcDelta(output.toBatch()).toList()
        weight = optimizer.adapt(weight = weight, dw = delta.toBatch())
        return delta.toBatch()
    }
}

fun <T> NetworkBuilder.D2<T>.positionEmbedding(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process = PositionEmbeddingD2(
        outputX = inputX,
        outputY = inputY,
        optimizer = optimizer.d2(inputX, inputY),
        weight = initializer.d2(
            input = listOf(inputX, inputY),
            output = listOf(inputX, inputY),
            x = inputX,
            y = inputY,
        ),
    ),
)

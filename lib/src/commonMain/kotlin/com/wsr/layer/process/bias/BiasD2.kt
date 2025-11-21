package com.wsr.layer.process.bias

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class BiasD2(
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
        val output = input.toList() + weight
        val delta = calcDelta(output.toBatch())
        weight = optimizer.adapt(weight = weight, dw = delta)
        return delta
    }
}

fun <T> NetworkBuilder.D2<T>.bias(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(0f)) =
    addProcess(
        process = BiasD2(
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

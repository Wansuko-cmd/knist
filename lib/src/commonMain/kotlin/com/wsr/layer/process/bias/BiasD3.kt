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
class BiasD3(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> =
        (input.toList() + weight).toBatch()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = input.toList() + weight
        val delta = calcDelta(output.toBatch())
        weight = optimizer.adapt(weight = weight, dw = delta)
        return delta
    }
}

fun <T> NetworkBuilder.D3<T>.bias(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(0f)) =
    addProcess(
        process = BiasD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            optimizer = optimizer.d3(inputX, inputY, inputZ),
            weight = initializer.d3(
                input = listOf(inputX, inputY, inputZ),
                output = listOf(inputX, inputY, inputZ),
                x = inputX,
                y = inputY,
                z = inputZ,
            ),
        ),
    )

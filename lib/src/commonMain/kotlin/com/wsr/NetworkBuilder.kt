package com.wsr

import com.wsr.optimizer.Optimizer
import com.wsr.output.Output
import com.wsr.process.Process
import com.wsr.reshape.Reshape
import kotlin.random.Random

sealed interface NetworkBuilder<I : IOType, O : IOType> {
    val layers: List<Layer>
    val optimizer: Optimizer
    val random: Random

    @ConsistentCopyVisibility
    data class D1<I : IOType> internal constructor(
        val inputSize: Int,
        override val layers: List<Layer>,
        override val optimizer: Optimizer,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addProcess(process: Process.D1): D1<I> = copy(
            layers = layers + process,
            inputSize = process.outputSize,
        )

        fun addOutput(output: Output.D1) = Network<I, IOType.D1>(layers + output)

        fun repeat(times: Int, builder: D1<I>.(index: Int) -> D1<I>): D1<I> =
            (0 until times).fold(this) { acc, i -> acc.builder(i) }
    }

    @ConsistentCopyVisibility
    data class D2<I : IOType> internal constructor(
        val inputX: Int,
        val inputY: Int,
        override val layers: List<Layer>,
        override val optimizer: Optimizer,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addProcess(process: Process.D2): D2<I> = copy(
            layers = layers + process,
            inputX = process.outputX,
            inputY = process.outputY,
        )

        fun addReshape(reshape: Reshape.D2ToD1): D1<I> = D1(
            layers = layers + reshape,
            inputSize = reshape.outputSize,
            optimizer = optimizer,
            random = random,
        )

        fun repeat(times: Int, builder: D2<I>.(index: Int) -> D2<I>): D2<I> =
            (0 until times).fold(this) { acc, i -> this.builder(i) }
    }

    @ConsistentCopyVisibility
    data class D3<I : IOType> internal constructor(
        val inputX: Int,
        val inputY: Int,
        val inputZ: Int,
        override val layers: List<Layer>,
        override val optimizer: Optimizer,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addProcess(process: Process.D3): D3<I> = copy(
            layers = layers + process,
            inputX = process.outputX,
            inputY = process.outputY,
            inputZ = process.outputZ,
        )

        fun addReshape(reshape: Reshape.D3ToD2): D2<I> = D2(
            layers = layers + reshape,
            inputX = reshape.outputX,
            inputY = reshape.outputY,
            optimizer = optimizer,
            random = random,
        )

        fun addReshape(reshape: Reshape.D3ToD1): D1<I> = D1(
            layers = layers + reshape,
            inputSize = reshape.outputSize,
            optimizer = optimizer,
            random = random,
        )

        fun repeat(times: Int, builder: D3<I>.(index: Int) -> D3<I>): D3<I> =
            (0 until times).fold(this) { acc, i -> this.builder(i) }
    }

    companion object {
        fun inputD1(inputSize: Int, optimizer: Optimizer, seed: Int? = null) = D1<IOType.D1>(
            inputSize = inputSize,
            optimizer = optimizer,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )

        fun inputD2(x: Int, y: Int, optimizer: Optimizer, seed: Int? = null) = D2<IOType.D2>(
            inputX = x,
            inputY = y,
            optimizer = optimizer,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )

        fun inputD3(x: Int, y: Int, z: Int, optimizer: Optimizer, seed: Int? = null) = D3<IOType.D3>(
            inputX = x,
            inputY = y,
            inputZ = z,
            optimizer = optimizer,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )
    }
}

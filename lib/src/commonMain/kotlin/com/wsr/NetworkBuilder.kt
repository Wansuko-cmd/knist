package com.wsr

import com.wsr.converter.Converter
import com.wsr.converter.linear.LinearD1
import com.wsr.converter.linear.LinearD2
import com.wsr.converter.linear.LinearD3
import com.wsr.optimizer.Optimizer
import com.wsr.output.Output
import com.wsr.process.Process
import com.wsr.reshape.Reshape
import kotlin.random.Random

sealed interface NetworkBuilder<I, O> {
    val input: Converter
    val layers: List<Layer>
    val optimizer: Optimizer
    val random: Random

    data class D1<I>(
        val inputSize: Int,
        override val input: Converter,
        override val layers: List<Layer>,
        override val optimizer: Optimizer,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addProcess(process: Process.D1): D1<I> = copy(
            layers = layers + process,
            inputSize = process.outputSize,
        )

        fun addOutput(output: Output.D1) = Network<I, IOType.D1>(
            inputConverter = input,
            outputConverter = LinearD1(inputSize),
            layers = layers + output,
        )

        fun <O> addOutput(output: Output.D1, converter: Converter.D1<O>) = Network<I, O>(
            inputConverter = input,
            outputConverter = converter,
            layers = layers + output,
        )

        fun repeat(times: Int, builder: D1<I>.(index: Int) -> D1<I>): D1<I> =
            (0 until times).fold(this) { acc, i -> acc.builder(i) }
    }

    data class D2<I>(
        val inputX: Int,
        val inputY: Int,
        override val input: Converter,
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
            input = input,
            layers = layers + reshape,
            inputSize = reshape.outputSize,
            optimizer = optimizer,
            random = random,
        )

        fun repeat(times: Int, builder: D2<I>.(index: Int) -> D2<I>): D2<I> =
            (0 until times).fold(this) { acc, i -> this.builder(i) }
    }

    data class D3<I>(
        val inputX: Int,
        val inputY: Int,
        val inputZ: Int,
        override val input: Converter,
        override val layers: List<Layer>,
        override val optimizer: Optimizer,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D3> {
        fun addProcess(process: Process.D3): D3<I> = copy(
            layers = layers + process,
            inputX = process.outputX,
            inputY = process.outputY,
            inputZ = process.outputZ,
        )

        fun addReshape(reshape: Reshape.D3ToD2): D2<I> = D2(
            input = input,
            layers = layers + reshape,
            inputX = reshape.outputX,
            inputY = reshape.outputY,
            optimizer = optimizer,
            random = random,
        )

        fun addReshape(reshape: Reshape.D3ToD1): D1<I> = D1(
            input = input,
            layers = layers + reshape,
            inputSize = reshape.outputSize,
            optimizer = optimizer,
            random = random,
        )

        fun repeat(times: Int, builder: D3<I>.(index: Int) -> D3<I>): D3<I> =
            (0 until times).fold(this) { acc, i -> this.builder(i) }
    }

    companion object
}

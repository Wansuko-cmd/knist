package dataset.mnist

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import maxIndex

@Serializable
data class PixelConverter(override val outputX: Int, override val outputY: Int) : Converter.D2<List<Float>>() {
    override fun encode(input: List<List<Float>>): Batch<IOType.D2> = input
        .map { IOType.d2(listOf(28, 28), it) }.toBatch()

    override fun decode(input: Batch<IOType.D2>): List<List<Float>> = input.toList().map {
        it.value.toFloatArray().toList()
    }
}

fun NetworkBuilder.Companion.inputPx(x: Int, y: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD2(
    converter = PixelConverter(x, y),
    optimizer = optimizer,
    initializer = initializer,
)

@Serializable
data class LabelConverter(override val outputSize: Int) : Converter.D1<Int>() {
    override fun encode(input: List<Int>): Batch<IOType.D1> = input.map { input ->
        IOType.d1(10) { if (input == it) 1f else 0f }
    }.toBatch()

    override fun decode(input: Batch<IOType.D1>): List<Int> = input.toList().map {
        it.value.toFloatArray().toTypedArray().maxIndex()
    }
}

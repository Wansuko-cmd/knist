package mnist

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.reduction.maxIndex
import com.wsr.knist.batch.unwrap
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
data class PixelConverter(override val i: Int, override val j: Int) : Converter.D2<List<List<Float>>>() {
    override fun encode(input: List<List<Float>>): Batch<IOType.D2> {
        val value = input.flatten().toFloatArray()
        return Batch.d2(size = input.size, i = i, j = j, value = value)
    }

    override fun decode(input: Batch<IOType.D2>): List<List<Float>> = input.unwrap()
        .toList()
        .chunked(input.size)
}

@Serializable
data class LabelConverter(override val i: Int) : Converter.D1<List<Int>>() {
    override fun encode(input: List<Int>): Batch<IOType.D1> {
        val value = FloatArray(input.size * i)
        repeat(input.size) { batchIndex ->
            val label = input[batchIndex]
            value[batchIndex * i + label] = 1f
        }
        return Batch.d1(size = input.size, i = i, value = value)
    }

    override fun decode(input: Batch<IOType.D1>): List<Int> = input.maxIndex()
        .unwrap()
        .map { it.toInt() }
}

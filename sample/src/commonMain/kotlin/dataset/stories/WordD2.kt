package dataset.stories

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.reduction.maxIndex
import com.wsr.knist.batch.shape.toList
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class WordD2(private val words: List<String>, private val length: Int, private val unknownIndex: Int) : Converter.D2<List<List<String>>>() {
    override val outputI = length
    override val outputJ = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index }.toMap()

    override fun encode(input: List<List<String>>): Batch<IOType.D2> {
        val result = FloatArray(input.size * outputI * outputJ)
        repeat(input.size) { b ->
            val offset = b * outputI * outputJ
            val text = input[b]
            text.forEachIndexed { index, word ->
                val id = wordToId[word] ?: unknownIndex
                result[offset + index * outputJ + id] = 1f
            }
        }
        return Batch.d2(size = input.size, i = outputI, j = outputJ, value = result)
    }

    override fun decode(input: Batch<IOType.D2>): List<List<String>> = input.maxIndex(axis = 1).toList()
        .map { input ->
            val input = input.value.toFloatArray()
            (0 until length).map { words[input[it].toInt()] }
        }
}

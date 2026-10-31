package dataset.stories

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toList
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class WordsD1(override val outputI: Int, private val words: List<String>, private val unknownIndex: Int, private val paddingIndex: Int) : Converter.D1<List<List<String>>>() {
    val vocabSize = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index.toFloat() }.toMap()

    override fun encode(input: List<List<String>>): Batch<IOType.D1> = input.map { sentence ->
        val tokenIds = sentence
            .take(outputI)
            .map { wordToId[it] ?: unknownIndex.toFloat() }

        FloatArray(outputI) { paddingIndex.toFloat() }
            .apply { tokenIds.toFloatArray().copyInto(this) }
            .let { IOType.d1(it) }
    }.toBatch()

    override fun decode(input: Batch<IOType.D1>): List<List<String>> = input.toList().map { input ->
        input.unwrap()
            .toList()
            .mapNotNull { id ->
                val index = id.toInt()
                if (index == paddingIndex) null else words.getOrNull(index)
            }
    }
}

fun wordsD1(maxLength: Int, words: List<String>, unknownIndex: Int, paddingIndex: Int): WordsD1 {
    require(unknownIndex in words.indices) { "unknownIndex must be within words range." }
    require(paddingIndex in words.indices) { "paddingIndex must be within words range." }

    return WordsD1(
        outputI = maxLength,
        words = words,
        unknownIndex = unknownIndex,
        paddingIndex = paddingIndex,
    )
}

@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")

package stories

import com.wsr.Network
import dataset.storeis.MAX_LENGTH
import dataset.storeis.createTinyStoriesModel
import dataset.storeis.tokenize
import kotlin.test.Test

private const val SEED = 0

class TinyStoriesTest {
    @Test
    fun `TinyStoriesモデルの出力を確認`() {
        val network = createTinyStoriesModel(SEED)
        val story = network.createStories(beginning = "One day, a sheep named Bob was very happy.", maxLength = 300)
        println(story)
    }

    private fun Network<List<String>, List<String>>.createStories(beginning: String, maxLength: Int): String {
        val text = tokenize(beginning).take(MAX_LENGTH).toMutableList()
        repeat(maxLength) {
            val input = text.takeLast(MAX_LENGTH)
            if (input.last() == "<EOS>") return@repeat
            val expect = this.expect(input)[input.lastIndex]
            text.add(expect)
        }
        return text.joinToString(" ")
            .replace(Regex(" ([!?.]) "), ".\n")
            .replace(Regex(" (,)"), "$1")
    }
}

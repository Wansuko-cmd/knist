package dataset.storeis

import java.io.File
import kotlin.sequences.forEach

/**
 * 語彙を構築
 * 特殊トークン + 頻度上位の単語で構成
 */
fun createWordList(
    path: String,
    maxSize: Int,
): List<String> {
    val wordCount = mutableMapOf<String, Int>()
    File(path)
        .useLines { lines ->
            lines
                .flatMap { tokenize(it) }
                .forEach { token ->
                    wordCount[token] = wordCount.getOrDefault(token, 0) + 1
                }
        }

    // 特殊トークン（必ず語彙に含める）
    val specialTokens = listOf(
        "<PAD>", // パディング (index 0)
        "<UNK>", // 未知語 (index 1)
        "<EOS>",
        "<NUM>", // 数値 (index 3)
    )

    // 頻度順にソート（特殊トークンを除く）
    val topWords = wordCount.entries
        .filter { it.key !in specialTokens }
        .sortedByDescending { it.value }
        .take(maxSize - specialTokens.size)
        .map { it.key }

    return specialTokens + topWords
}

/**
 * テキストを基本的なトークンに分割（特殊トークンなし）
 * - 小文字化
 * - 数値を<NUM>に統一
 * - 句読点を独立したトークンに分離（ただしアポストロフィは単語の一部として保持）
 */
fun tokenize(text: String): List<String> = text
    .lowercase()
    // 数値を<NUM>に統一
    .replace(Regex("\\b\\d+\\b"), "<NUM>")
    // 不要な文字を削除
    .replace(Regex("([;:\"\'\n])"), "")
    // 句読点の前後にスペースを追加
    .replace(Regex("([.,!?])"), " $1 ")
    // 連続する空白を単一スペースに
    .replace(Regex("\\s+"), " ")
    .replace("<|endoftext|>", "<EOS>")
    .trim()
    .split(" ")
    .filter { it.isNotBlank() }

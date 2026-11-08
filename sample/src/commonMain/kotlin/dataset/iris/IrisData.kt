package dataset.iris

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.core.IOType
import dataset.resource
import okio.FileSystem
import okio.SYSTEM
import okio.buffer
import okio.use

private val LABELS = listOf("Iris-setosa", "Iris-versicolor", "Iris-virginica")

// 複数特徴量 -> [バッチ, 4]
fun List<IrisData>.toFeatures(): Batch<IOType.D1> = Batch.d1(
    size = size,
    i = 4,
    value = flatMap { it.features }.toFloatArray(),
)

// 複数ラベル -> [バッチ, 3]
fun List<IrisData>.toLabels(): Batch<IOType.D1> = Batch.d1(
    size = size,
    i = 3,
    value = FloatArray(size * 3).also { value ->
        repeat(size) { b ->
            val label = this[b].label
            value[b * 3 + label] = 1f
        }
    },
)

data class IrisData(val features: List<Float>, val label: Int)

data object IrisDatasets {
    // UCI Machine Learning Repository の iris.data 形式（例: 5.1,3.5,1.4,0.2,Iris-setosa）
    fun read(path: String): List<IrisData> = try {
        FileSystem.SYSTEM.resource(path).buffer().use { source ->
            generateSequence { source.readUtf8Line() }
                .filter { it.isNotBlank() }
                .map { line ->
                    val columns = line.split(",")
                    IrisData(
                        features = columns.take(4).map { it.toFloat() },
                        label = LABELS.indexOf(columns[4].trim()).also { require(it >= 0) { "未知のラベル: ${columns[4]}" } },
                    )
                }
                .toList()
        }
    } catch (e: Exception) {
        println("resource/irisにIrisデータセット(iris.data)を入れてください")
        throw e
    }
}

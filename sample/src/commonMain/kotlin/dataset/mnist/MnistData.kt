package dataset.mnist

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d3
import com.wsr.knist.core.IOType
import dataset.resource
import okio.BufferedSource
import okio.FileSystem
import okio.GzipSource
import okio.SYSTEM
import okio.buffer
import okio.use

fun List<MnistData>.toImages(): Batch<IOType.D3> = Batch.d3(
    size = size, i = 1, j = 28, k = 28,
    value = flatMap { it.pixels }.toFloatArray(),
)

// 複数ラベル -> [バッチ, label]
fun List<MnistData>.toLabels(): Batch<IOType.D1> = Batch.d1(
    size = size, i = 10,
    value = FloatArray(size * 10).also { value ->
        repeat(size) { b ->
            val label = this[b].label
            value[b * 10 + label] = 1f
        }
    },
)

data class MnistData(val pixels: List<Float>, val label: Int)

data object MnistDatasets {
    fun read(imagePath: String, labelPath: String): List<MnistData> = try {
        gzipBuffer(imagePath).use { imgIn ->
            gzipBuffer(labelPath).use { lblIn ->
                imgIn.skip(4)
                lblIn.skip(4)

                val numOfImages = imgIn.readInt()
                val height = imgIn.readInt()
                val width = imgIn.readInt()
                val numOfLabels = lblIn.readInt()

                val numOfPixel = height * width

                List(numOfImages) {
                    MnistData(
                        pixels = List(numOfPixel) { imgIn.readUInt().toFloat() },
                        label = lblIn.readUInt(),
                    )
                }
            }
        }
    } catch (e: Exception) {
        println("resource/mnistにMNISTデータセットを入れてください")
        throw e
    }

    private fun gzipBuffer(path: String): BufferedSource {
        val source = FileSystem.SYSTEM.resource(path)
        return GzipSource(source).buffer()
    }

    private fun BufferedSource.readUInt() = readByte().toInt() and 0xFF
}

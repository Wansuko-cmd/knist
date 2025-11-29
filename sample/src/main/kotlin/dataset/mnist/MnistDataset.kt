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
import java.io.DataInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import kotlinx.serialization.Serializable
import maxIndex

data class MnistDataset(val pixels: List<Float>, val label: Int, val imageSize: Int) {
    override fun toString(): String = pixels
        .chunked(imageSize)
        .joinToString("\n") { row ->
            row
                .joinToString { column ->
                    if (column > 0) "■" else "□"
                }.replace(",", "")
        }

    companion object {
        private const val LABEL_PATH = "../train-labels-idx1-ubyte.gz"
        private const val IMAGE_PATH = "../train-images-idx3-ubyte.gz"
        private const val PIXEL_DEPTH = 255

        fun read(): List<MnistDataset> {
            val labelPath = Paths.get(LABEL_PATH)
            val imagePath = Paths.get(IMAGE_PATH)
            val labelStream = DataInputStream(GZIPInputStream(Files.newInputStream(labelPath)))
            val imageStream = DataInputStream(GZIPInputStream(Files.newInputStream(imagePath)))
            labelStream.skip(4)
            imageStream.skip(4)
            val labelSize = labelStream.readInt()
            val imageSize = imageStream.readInt()
            val imageHeight = imageStream.readInt()
            val imageWidth = imageStream.readInt()
            val labels = (1..labelSize).map { labelStream.readUnsignedByte() }
            val images =
                (1..imageSize)
                    .map {
                        (1..imageHeight * imageWidth)
                            .map { imageStream.readUnsignedByte() }
                            .map { it.toFloat() - (PIXEL_DEPTH / 2f) }
                            .map { it / PIXEL_DEPTH }
                    }
            return labels.zip(images) { label, image -> MnistDataset(image, label, imageWidth) }
        }
    }
}

@Serializable
data class PixelConverter(override val outputX: Int, override val outputY: Int) : Converter.D2<List<Float>>() {
    override fun encode(input: List<List<Float>>): Batch<IOType.D2> = input
        .map { IOType.d2(listOf(28, 28), it) }.toBatch()

    override fun decode(input: Batch<IOType.D2>): List<List<Float>> = input.toList().map { it.value.toList() }
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

    override fun decode(input: Batch<IOType.D1>): List<Int> = input.toList().map { it.value.toTypedArray().maxIndex() }
}

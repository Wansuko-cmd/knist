package dataset.mnist

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import java.io.DataInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import kotlinx.serialization.Serializable
import maxIndex

data class MnistDataset(val pixels: List<Double>, val label: Int, val imageSize: Int) {
    override fun toString(): String = pixels
        .chunked(imageSize)
        .joinToString("\n") { row ->
            row
                .joinToString { column ->
                    if (column > 0) "■" else "□"
                }.replace(",", "")
        }

    companion object {
        private const val LABEL_PATH = "train-labels-idx1-ubyte.gz"
        private const val IMAGE_PATH = "train-images-idx3-ubyte.gz"
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
                            .map { it.toDouble() - (PIXEL_DEPTH / 2.0) }
                            .map { it / PIXEL_DEPTH }
                    }
            return labels.zip(images) { label, image -> MnistDataset(image, label, imageWidth) }
        }
    }
}

@Serializable
data class PixelConverter(override val outputX: Int, override val outputY: Int) : Converter.D2<List<Double>>() {
    override fun encode(input: List<List<Double>>): List<IOType.D2> = input
        .map { IOType.d2(listOf(28, 28), it) }

    override fun decode(input: List<IOType.D2>): List<List<Double>> = input.map { it.value.toList() }
}

fun NetworkBuilder.Companion.inputPx(
    x: Int,
    y: Int,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    seed: Int? = null,
) = inputD2(
    converter = PixelConverter(x, y),
    optimizer = optimizer,
    initializer = initializer,
    seed = seed,
)

@Serializable
data class LabelConverter(override val outputSize: Int) : Converter.D1<Int>() {
    override fun encode(input: List<Int>): List<IOType.D1> = input.map { input ->
        IOType.d1(10) { if (input == it) 1.0 else 0.0 }
    }

    override fun decode(input: List<IOType.D1>): List<Int> = input.map { it.value.toTypedArray().maxIndex() }
}

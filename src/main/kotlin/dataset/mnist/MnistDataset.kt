package dataset.mnist

import java.io.DataInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

data class MnistDataset(
    val pixels: List<Double>,
    val label: Int,
    val imageSize: Int,
) {
    override fun toString(): String =
        pixels
            .chunked(imageSize)
            .joinToString("\n") { row ->
                row.joinToString { column ->
                    if (column > 0) "■" else "□"
                }.replace(",", "")
            }

    companion object {
        private const val LABEL_PATH = "src/main/resources/train-labels-idx1-ubyte.gz"
        private const val IMAGE_PATH = "src/main/resources/train-images-idx3-ubyte.gz"
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
            val images = (1..imageSize)
                .map {
                    (1..imageHeight * imageWidth)
                        .map { imageStream.readUnsignedByte() }
                        .filterIndexed { index, _ -> index % 2 == 0 }
                        .map { it.toDouble() - (PIXEL_DEPTH / 2.0) }
                        .map { it / PIXEL_DEPTH }
                }.filterIndexed { index, _ -> index % 2 == 0 }
            return labels.zip(images) { label, image -> MnistDataset(image, label, imageWidth / 2) }
        }
    }
}

package mnist

import java.io.DataInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

data class MnistDataset(val pixels: List<Float>, val label: Int, val imageSize: Int) {
    companion object {
        private const val PIXEL_DEPTH = 255
        private val BASE_DIR = System.getenv("MNIST_DATA_DIR") ?: "."

        fun read(imagePath: String, labelPath: String): List<MnistDataset> {
            val imagePath = Paths.get(BASE_DIR, imagePath)
            val imageStream = DataInputStream(GZIPInputStream(Files.newInputStream(imagePath)))

            val labelPath = Paths.get(BASE_DIR, labelPath)
            val labelStream = DataInputStream(GZIPInputStream(Files.newInputStream(labelPath)))

            imageStream.skip(4)
            labelStream.skip(4)

            val imageSize = imageStream.readInt()
            val imageHeight = imageStream.readInt()
            val imageWidth = imageStream.readInt()
            val labelSize = labelStream.readInt()

            val images = (1..imageSize)
                .map {
                    (1..imageHeight * imageWidth)
                        .map { imageStream.readUnsignedByte() }
                        .map { it.toFloat() - (PIXEL_DEPTH / 2f) }
                        .map { it / PIXEL_DEPTH }
                }
            val labels = (1..labelSize).map { labelStream.readUnsignedByte() }
            return images.zip(labels) { image, label -> MnistDataset(image, label, imageWidth) }
        }
    }
}

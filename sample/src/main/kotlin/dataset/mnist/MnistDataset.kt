package dataset.mnist

import java.io.DataInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

data class MnistDataset(val pixels: List<Float>, val label: Int, val imageSize: Int) {
    companion object {
        private const val BASE_DIR = "src/main/resources"

        fun read(imagePath: String, labelPath: String): List<MnistDataset> {
            val imagePath = Paths.get(BASE_DIR, imagePath)
            val imageStream = DataInputStream(GZIPInputStream(Files.newInputStream(imagePath)))

            val labelPath = Paths.get(BASE_DIR, labelPath)
            val labelStream = DataInputStream(GZIPInputStream(Files.newInputStream(labelPath)))

            imageStream.skip(4)
            labelStream.skip(4)

            val numOfImages = imageStream.readInt()
            val imageHeight = imageStream.readInt()
            val imageWidth = imageStream.readInt()
            val numObLabels = labelStream.readInt()

            val images = List(numOfImages) {
                List(imageHeight * imageWidth) {
                    imageStream.readUnsignedByte().toFloat()
                }
            }
            val labels = List(numObLabels) { labelStream.readUnsignedByte() }
            return images.zip(labels) { image, label -> MnistDataset(image, label, imageWidth) }
        }
    }
}

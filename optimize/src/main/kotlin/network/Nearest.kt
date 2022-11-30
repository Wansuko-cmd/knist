package network

import dataset.iris.IrisDataset
import dataset.wine.WineDataset
import kotlin.math.pow

class Nearest(private val pattern: List<IrisDataset>) {
    fun expect(input: IrisDataset): Int =
        pattern.minBy { it.calcDistance(input) }.label

    private fun IrisDataset.calcDistance(other: IrisDataset): Double =
        listOf(this.petalWidth, this.petalLength, this.sepalLength, this.sepalWidth)
            .zip(listOf(other.petalWidth, other.petalLength, other.sepalLength, other.sepalWidth))
            .map { (left, right) -> left - right }
            .sumOf { it.pow(2) }
}

class WineNearest(private val pattern: List<WineDataset>) {
    fun expect(input: WineDataset): Int =
        pattern.minBy { it.calcDistance(input) }.label

    private fun WineDataset.calcDistance(other: WineDataset): Double =
        this.toList()
            .zip(other.toList())
            .map { (left, right) -> left - right }
            .sumOf { it }

    private fun WineDataset.toList(): List<Double> = listOf(
        alcohol,
        malicAcid,
        ash,
        alcalinityOfAsh,
        magnesium,
        totalPhenols,
        flavanoids,
        nonflavAnoidPhenols,
        proanthocyanins,
        colorIntensity,
        hue,
        wines,
        proline,
    )
}

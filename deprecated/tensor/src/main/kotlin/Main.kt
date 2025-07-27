package tensor

import dataset.iris.IrisDataset
import dataset.iris.irisDatasets
import tensor.operation.times
import tensor.tensor.function.relu
import tensor.tensor.function.sigmoid
import tensor.tensor.operation.minus
import tensor.tensor.operation.plus
import kotlin.random.Random

val random = Random(4)

fun main() {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val weight: List<List<MutableList<Double>>> =
        listOf(
            List(4) { MutableList(50) { random.nextDouble(-1.0, 1.0) } },
            List(50) { MutableList(3) { random.nextDouble(-1.0, 1.0) } }
        )
    (0..100).forEach { _ ->
        train.forEach { train(it, weight) }
    }
    println(test.count { test(it, weight) == it.label }.toDouble() / test.size)
}

fun train(dataset: IrisDataset, weight: List<List<MutableList<Double>>>) {
    val layer0 = listOf(
        const(dataset.petalWidth),
        const(dataset.petalLength),
        const(dataset.sepalWidth),
        const(dataset.sepalLength),
    )
    val layer1 = mutableListOf<Tensor>()
    for (i in 0 until 50) {
        layer0
            .mapIndexed { index, tensor -> const(weight[0][index][i]) * tensor }
            .reduce { acc: Tensor, mul: Tensor -> acc + mul }
            .let { layer1.add(it) }
    }
    val layer2 = layer1.map { relu(it) }
    val layer3 = mutableListOf<Tensor>()
    for (i in 0 until 3) {
        layer2
            .mapIndexed { index, tensor -> const(weight[1][index][i]) * tensor }
            .reduce { acc: Tensor, mul: Tensor -> acc + mul }
            .let { layer3.add(it) }
    }
    val layer4 = layer3.map { sigmoid(it) }
    layer4
        .onEachIndexed { index, tensor -> tensor.grad = tensor.output - if (dataset.label == index) 0.9 else 0.1 }
        .onEach { it.calcGrad() }
        .forEach { it.backwardBefore() }
    for (i in weight[0].indices) {
        for (j in weight[0][i].indices) {
            weight[0][i][j] -= 0.01 * layer1[j].grad
        }
    }
//    println(layer1.map { it.output })
    for (i in weight[1].indices) {
        for (j in weight[1][i].indices) {
            weight[1][i][j] -= 0.01 * layer3[j].grad
        }
    }
}

fun test(dataset: IrisDataset, weight: List<List<MutableList<Double>>>): Int {
    val layer0 = listOf(
        const(dataset.petalWidth),
        const(dataset.petalLength),
        const(dataset.sepalWidth),
        const(dataset.sepalLength),
    )
    val layer1 = mutableListOf<Tensor>()
    for (i in 0 until 50) {
        layer0
            .mapIndexed { index, tensor -> const(weight[0][index][i]) * tensor }
            .reduce { acc: Tensor, mul: Tensor -> acc + mul }
            .let { layer1.add(it) }
    }
    val layer2 = layer1.map { relu(it) }
    val layer3 = mutableListOf<Tensor>()
    for (i in 0 until 3) {
        layer2
            .mapIndexed { index, tensor -> const(weight[1][index][i]) * tensor }
            .reduce { acc: Tensor, mul: Tensor -> acc + mul }
            .let { layer3.add(it) }
    }
    val layer4 = layer3.map { sigmoid(it) }
    layer4.mapIndexed { index, tensor -> tensor - const(if (dataset.label == index) 0.9 else 0.1) }
    return layer4.map { it.output }.maxIndex()
}

fun <T : Comparable<T>> List<T>.maxIndex(): Int =
    this.foldIndexed(null) { index: Int, acc: Pair<Int, T>?, element: T ->
        when {
            acc == null -> index to element
            acc.second > element -> acc
            else -> index to element
        }
    }?.first ?: throw Exception()

import dataset.iris.datasets

fun main() {
    val network = Network.create(listOf(4, 50, 3))
    val (train, test) = datasets.shuffled().chunked(120)
    (1..1000).forEach { epoc ->
        println("epoc: $epoc")
        train.forEach { data ->
            network.train(
                input = listOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalLength,
                    data.sepalWidth,
                ),
                label = data.label,
            )
        }
    }
    test.count { data ->
        network.expect(
            input = listOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ).also { println("Except: $it, label: ${data.label}") } == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}

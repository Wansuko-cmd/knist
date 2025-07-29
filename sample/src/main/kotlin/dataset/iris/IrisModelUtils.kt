package dataset.iris

fun createIrisModel(
    epoc: Int,
    seed: Int? = null,
) {
//    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
//    val network = Network.create0d(
//        Input0dLayer(4),
//        listOf(
//            Affine(50, ::relu),
//            Bias0d(::relu)
//        ),
//        Softmax0d(3) { numOfNeuron, activationFunction -> Affine(numOfNeuron, activationFunction) },
//        random = seed?.let { Random(it) } ?: Random,
//        rate = 0.01,
//    )
//    (1..epoc).forEach { epoc ->
////        println("epoc: $epoc")
//        train.forEach { data ->
//            network.train(
//                input = listOf(
//                    data.petalLength,
//                    data.petalWidth,
//                    data.sepalLength,
//                    data.sepalWidth,
//                ),
//                label = data.label,
//            )
//        }
//    }
//    test.count { data ->
//        network.expect(
//            input = listOf(
//                data.petalLength,
//                data.petalWidth,
//                data.sepalLength,
//                data.sepalWidth,
//            ),
//        ) == data.label
//    }.let { println(it.toDouble() / test.size.toDouble()) }
}

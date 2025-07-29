import com.wsr.layer.affine.Affine1d
import com.wsr.layer.bias.Bias1d
import com.wsr.Network2
import com.wsr.layer.function.Relu1d
import com.wsr.layer.function.Softmax1d
import dataset.iris.irisDatasets

fun main() {
    createIrisModel2(1000)
}

private fun createIrisModel2(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network2 = Network2(
        listOf(
            Affine1d(4, 50, 0.01),
            Bias1d(50, 50, 0.01),
            Relu1d(50, 50),
            Affine1d(50, 3, 0.01),
            Softmax1d(3, 3),
        ),
    )
    (1..epoc).forEach { epoc ->
        train.forEach { data ->
            network2.train(
                input = arrayOf(
                    data.petalLength,
                    data.petalWidth,
                    data.sepalLength,
                    data.sepalWidth,
                ),
                label = Array(3) { if (data.label == it) 1.0 else 0.0 },
            )
        }
    }
    test.count { data ->
        network2.expect(
            input = arrayOf(
                data.petalLength,
                data.petalWidth,
                data.sepalLength,
                data.sepalWidth,
            ),
        ).toList().maxIndex() == data.label
    }.let { println(it.toDouble() / test.size.toDouble()) }
}

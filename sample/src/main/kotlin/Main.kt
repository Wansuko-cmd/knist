import com.wsr.Network2
import com.wsr.layer.affine.affine1d
import com.wsr.layer.bias.bias1d
import com.wsr.layer.function.relu1d
import com.wsr.layer.function.softmax1d
import dataset.iris.irisDatasets

fun main() {
    createIrisModel2(1000)
}

private fun createIrisModel2(
    epoc: Int,
    seed: Int? = null,
) {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network2 = Network2.Builder(numOfInput = 4, rate = 0.01)
        .affine1d(50).bias1d().relu1d()
        .affine1d(3).softmax1d()
        .build()

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

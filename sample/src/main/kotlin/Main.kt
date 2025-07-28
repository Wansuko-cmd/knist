import com.wsr.Affine1d
import com.wsr.Bias1d
import com.wsr.Network
import com.wsr.Network2
import com.wsr.Relu1d
import com.wsr.Sigmoid1d
import com.wsr.common.maxIndex
import com.wsr.common.relu
import com.wsr.layers.affine.Affine
import com.wsr.layers.bias.Bias0d
import com.wsr.layers.input.Input0dLayer
import com.wsr.layers.output.layer0d.Softmax0d
import dataset.iris.irisDatasets
import kotlin.random.Random

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
            Relu1d(),
            Bias1d(50, 0.01),
            Relu1d(),
            Affine1d(50, 3, 0.01),
            Sigmoid1d(),
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


//    val network = Network.create0d(
//        Input0dLayer(4),
//        listOf(
//            Affine(50, ::relu),
//            Bias0d(::relu),
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

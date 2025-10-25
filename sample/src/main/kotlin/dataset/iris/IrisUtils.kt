package dataset.iris

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.linear.inputD1
import com.wsr.optimizer.sgd.Sgd
import com.wsr.output.softmax.softmaxWithLoss
import com.wsr.process.affine.affine
import com.wsr.process.bias.bias
import com.wsr.process.function.relu.reLU
import maxIndex

fun createIrisModel(epoc: Int) {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network =
        NetworkBuilder
            .inputD1(inputSize = 4, optimizer = Sgd(0.01))
            .affine(neuron = 50)
            .bias()
            .reLU()
            .affine(neuron = 3)
            .softmaxWithLoss()

    (1..epoc).forEach { epoc ->
        train.forEach { data ->
            network.train(
                input =
                IOType.d1(
                    listOf(
                        data.petalLength,
                        data.petalWidth,
                        data.sepalLength,
                        data.sepalWidth,
                    ),
                ),
                label = IOType.d1(3) { if (data.label == it) 1.0 else 0.0 },
            )
        }
    }
    test
        .count { data ->
            network
                .expect(
                    input =
                    IOType.d1(
                        listOf(
                            data.petalLength,
                            data.petalWidth,
                            data.sepalLength,
                            data.sepalWidth,
                        ),
                    ),
                ).value
                .toTypedArray()
                .maxIndex() == data.label
        }.let { println(it.toDouble() / test.size.toDouble()) }
}

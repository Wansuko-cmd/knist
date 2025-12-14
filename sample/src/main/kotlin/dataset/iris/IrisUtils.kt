package dataset.iris

import com.wsr.NetworkBuilder
import com.wsr.converter.linear.inputD1
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.initializer.He
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import com.wsr.output.softmax.softmaxWithLoss
import com.wsr.process.compute.affine.affine
import com.wsr.process.compute.bias.bias
import com.wsr.process.compute.function.relu.reLU
import maxIndex

fun createIrisModel(epoc: Int) {
    val (train, test) = irisDatasets.shuffled() to irisDatasets.shuffled()
    val network =
        NetworkBuilder
            .inputD1(inputSize = 4, optimizer = Sgd(scheduler = Scheduler.Fix(0.01f)), initializer = He())
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
                label = IOType.d1(3) { if (data.label == it) 1f else 0f },
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
                .toFloatArray()
                .toTypedArray()
                .maxIndex() == data.label
        }.let { println(it.toFloat() / test.size.toFloat()) }
}

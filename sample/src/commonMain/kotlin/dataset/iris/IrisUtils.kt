package dataset.iris

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.elementwise.compare.eq
import com.wsr.knist.batch.reduction.maxIndex
import com.wsr.knist.batch.shape.toD1
import com.wsr.knist.core.IOType
import com.wsr.knist.core.reduction.sum
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.Network
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.create
import com.wsr.knist.network.initializer.He
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.adam.AdamW
import com.wsr.knist.network.output.softmax.softmaxWithLoss
import com.wsr.knist.network.port
import com.wsr.knist.network.process.compute.affine.affine
import com.wsr.knist.network.process.compute.bias.d1.bias
import com.wsr.knist.network.process.compute.function.relu.reLU
import com.wsr.knist.network.process.compute.norm.batch.d1.batchNorm
import kotlin.random.Random
import kotlinx.coroutines.runBlocking

private const val DATA_PATH = "iris/iris.data"

// 学習データ・評価データの分割比率
private const val TRAIN_RATIO = 0.8f

fun createIrisModel(epoch: Int, seed: Int? = null): Network.Src1.Sink1<Batch<IOType.D1>, Batch<IOType.D1>> = runBlocking {
    println("データ読み込み")
    val dataset = IrisDatasets.read(path = DATA_PATH).shuffled(Random(seed ?: 0))
    val trainSize = (dataset.size * TRAIN_RATIO).toInt()
    val train = dataset.take(trainSize)
    val test = dataset.drop(trainSize)

    // ニューラルネットワークを構築
    val network = Network.create(
        port = port(RawD1(i = 4)),
        optimizer = AdamW(scheduler = Scheduler.Fix(0.01f)),
        initializer = He(seed = seed),
    ) { input ->
        input
            .batchNorm()
            .affine(neuron = 16).bias().reLU()
            .affine(neuron = 16).bias().reLU()
            .affine(neuron = 3)
            .softmaxWithLoss()
    }

    println("訓練開始")
    repeat(epoch) { epoch ->
        println("epoch: $epoch")
        train.chunked(16).forEach { data ->
            network.train(input = data.toFeatures(), label = data.toLabels())
        }
    }

    println("評価開始")
    var count = 0
    test.chunked(16).forEach { data ->
        val expect = network.expect(data.toFeatures())
        val actual = data.toLabels()
        val correct = (actual.maxIndex() eq expect.maxIndex())
        count += correct.toD1().sum().unwrap().toInt()
    }
    println("${count / test.size.toFloat()}")

    network
}

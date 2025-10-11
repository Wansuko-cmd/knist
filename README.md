## 現状のインターフェイス

```kotlin
fun main() {
    val (train: List<List<MinistDataset>>, test: List<List<MnistDataset>>) = MnistDataset.read()

    val network: Network<IOType.D2, IOType.D1> = NetworkBuilder
        .inputD2(x = 28, y = 28, optimizer = Sgd(0.01), seed = seed)
        .convD1(filter = 16, kernel = 3).bias().reLU()
        .convD1(filter = 32, kernel = 3).bias().reLU()
        .reshapeToD1()
        .affine(neuron = 512).bias().reLU()
        .affine(neuron = 10)
        .softmaxWithLoss()

    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEach { batch: List<Pair<IOType.D2, IOType.D1>> ->
            network.train(
                input = batch.map { (pixels, _) -> pixels },
                label = batch.map { (_, label) -> label },
            )
        }
    }
    
    val expects = network.expect(input = test.map { (pixels, _) -> pixels })
    expects.zip(test.map { (_, label) -> label }).count { (expect, label) -> expect == label }
        .also { println(it.toDouble() / test.size.toDouble()) }
}
```

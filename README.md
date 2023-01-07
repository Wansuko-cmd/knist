## 将来的なインターフェイス（想定）

```kotlin
fun main() {
    val (train: List<List<MinistDataset>>, test: List<List<MnistDataset>>) = MnistDataset.read()

    val network: Network<List<List<List<Double>>>, Int> = 
        NetworkBuilder(rate = 0.01)
            .input2d(channel = 1, width = 28, height = 28)
            .conv2d(channel = 4, kernelSize = 5).bias2d().relu()
            .conv2d(channel = 8, kernelSize = 5).bias2d().relu()
            .affine(size = 50).bias0d().relu()
            .affine(size = 10).bias0d().softmax()
            .output0d()

    (1..epoc).forEach { epoc ->
        println("epoc: $epoc")
        train.forEach { batch: List<MnistDataset> ->
            network.train(
                input = batch.map { it.pixels },
                label = batch.map { it.label },
            )
        }
    }
    
    test.flatten().count { data ->
        network.expect(input = data.pixels) == data.label
    }.also { println(it.toDouble() / test.size.toDouble()) }
}
```

## 現状

practicalにてAIの実装中（速度は遅い）

## TODO

- バッチ処理対応
- インターフェイス修正
- C++を用いた高速化
- GPU対応

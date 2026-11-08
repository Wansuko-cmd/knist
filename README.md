# knist

## 概要

Kotlin製ニューラルネットワークライブラリ。

元々はKotlinでMNIST識別をしたくて作ったためknistとしています。

※ 現在開発中のため、予告なく破壊的変更が加わる可能性が非常に高いです。あらかじめご了承ください。

## ドキュメント(AI向け兼任)

- [docs/llms.txt](docs/llms.txt) : ドキュメントの索引
- [docs/llms-usage.txt](docs/llms-usage.txt) : 利用者向けの完全リファレンス（導入・モデル構築・学習・推論・保存）
- [docs/llms-custom.txt](docs/llms-custom.txt) : 拡張実装者向けリファレンス（カスタム層・出力層・オプティマイザ等の自作）

## コード例

1. モデルを定義

```kotlin
val network = Network.create(
    port = port(RawD3(i = 1, j = 28, k = 28)),
    optimizer = AdamW(scheduler = Scheduler.Fix(0.001f)),
    initializer = He(seed = seed),
) { input ->
    input
        .convD2(filter = 16, kernel = 5, padding = 2).bias().reLU()
        .convD2(filter = 32, kernel = 3, padding = 1).bias().reLU()
        .reshapeToD1()
        .layerNorm()
        .affine(neuron = 128).bias().reLU()
        .affine(neuron = 10)
        .softmaxWithLoss()
}
```

2. `train`関数を用いて学習

```kotlin
repeat(epoch) { epoch ->
    println("epoch: $epoch")
    train.chunked(256).forEach { data ->
        network.train(input = data.toImages(), label = data.toLabels())
    }
}
```

3. `expect`関数を用いて推測（入力・出力ともにバッチ単位）

```kotlin
val expected = network.expect(input = data.toImages())
```

sampleにてIris(MLP)とMNIST(CNN)とTinyStories(Transformer)の例を載せている

- [Iris](sample/src/commonMain/kotlin/dataset/iris/IrisUtils.kt)
- [MNIST](sample/src/commonMain/kotlin/dataset/mnist/MnistUtils.kt)
- [TinyStories](sample/src/commonMain/kotlin/dataset/stories/TinyStoriesUtils.kt)

## 導入方法

### **JVM / Android**

[JitPack](https://jitpack.io/#Wansuko-cmd/knist/) から導入可能

**settings.gradle.kts** にリポジトリを追加:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

**build.gradle.kts** に依存を追加:

```kotlin
dependencies {
    implementation("com.github.Wansuko-cmd.knist:network:<version>")
    
    // GPUを使う場合
    implementation("com.github.Wansuko-cmd.knist:gpu:<version>")
    
    // I/Oがsuspend関数のため必要
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:<version>")
}
```

### **Kotlin/Native**（macOS / Linux / Windows）

リポジトリをクローンして `publishToMavenLocal` することで利用可能

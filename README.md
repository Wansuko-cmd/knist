# knist

## 概要

Kotlin製ニューラルネットワークライブラリ。

元々はKotlinでMNIST識別をしたくて作ったためknistとしています

※ 現在開発中のため、予告なく破壊的変更が加わる可能性が非常に高いです。あらかじめご了承ください。

## ドキュメント(AI向け兼任)

- [docs/llms.txt](docs/llms.txt) : ドキュメントの索引
- [docs/llms-usage.txt](docs/llms-usage.txt) : 利用者向けの完全リファレンス（導入・モデル構築・学習・推論・保存）
- [docs/llms-custom.txt](docs/llms-custom.txt) : 拡張実装者向けリファレンス（カスタム層・出力層・オプティマイザ等の自作）

## コード例

1. モデルを定義

```kotlin
val network: Network.Src1.Sink1<List<List<Float>>, List<Int>> = Network.create(
    port = port(PixelConverter(28, 28)),
    optimizer = AdamW(Scheduler.Fix(0.001f)),
    initializer = He(seed = seed),
) { input ->
    input.reshapeToD1()
        .layerNorm()
        .affine(neuron = 256).bias().reLU()
        .affine(neuron = 128).bias().reLU()
        .affine(neuron = 10)
        .softmaxWithLoss(converter = { LabelConverter(inputI) })
}
```

2. `train`関数を用いて学習

```kotlin
runBlocking {
    repeat(epoch) { epoch ->
        println("epoch: $epoch")
        train.chunked(256).forEach { data ->
            network.train(
                input = data.map { it.pixels },
                label = data.map { it.label },
            )
        }
    }
}
```

3. `expect`関数を用いて推測（入力・出力ともにバッチ単位）

```kotlin
val expected: List<Int> = network.expect(input = data.map { it.pixels })
```

sampleにてMNIST(3層NN)とTinyStories(Transformer)の例を載せています

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

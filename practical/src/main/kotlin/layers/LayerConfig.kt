package layers

interface LayerConfig {
    val numOfNeuron: Int
    val activationFunction: (Double) -> Double
}

package network

data class LayerConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
    val type: LayerType,
)

data class InputConfig(val size: Int) {
    fun toLayoutConfig() = LayerConfig(size, { it }, LayerType.Input)
}

sealed class LayerType {
    object Input : LayerType()
    object Affine : LayerType()
}

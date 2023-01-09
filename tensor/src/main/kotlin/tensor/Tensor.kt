package tensor

abstract class Tensor(
    private val before: List<Tensor>,
    val output: Double,
) {
    var grad: Double = 0.0
    abstract fun calcGrad()

    fun backward() {
        grad = 1.0
        calcGrad()
        backwardBefore()
    }

    fun backwardBefore() {
        before.forEach { it.calcGrad() }
        before.forEach { it.backwardBefore() }
    }

    fun gradZero() {
        grad = 0.0
        before.forEach { it.gradZero() }
    }
}

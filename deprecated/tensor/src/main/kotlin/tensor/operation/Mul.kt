package tensor.operation

import tensor.Tensor

class Mul(
    private val left: Tensor,
    private val right: Tensor,
) : Tensor(before = listOf(left, right), output = left.output * right.output) {
    override fun calcGrad() {
        left.grad += right.output * grad
        right.grad += left.output * grad
    }
}

operator fun Tensor.times(other: Tensor) = Mul(this, other)

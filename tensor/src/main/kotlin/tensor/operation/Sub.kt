package tensor.tensor.operation

import tensor.Tensor

class Sub(
    private val left: Tensor,
    private val right: Tensor,
) : Tensor(before = listOf(left, right), output = left.output - right.output) {
    override fun calcGrad() {
        left.grad += grad
        right.grad += -grad
    }
}

operator fun Tensor.minus(other: Tensor) = Sub(this, other)

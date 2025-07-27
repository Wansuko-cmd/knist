package tensor.tensor.function

import tensor.Tensor

class ReLU(
    private val before: Tensor,
) : Tensor(before = listOf(before), output = if (before.output > 0.0) before.output else 0.0) {
    override fun calcGrad() {
        before.grad += if (before.output > 0.0) grad else 0.0
    }
}

fun relu(tensor: Tensor) = ReLU(tensor)

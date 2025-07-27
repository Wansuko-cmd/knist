package tensor.tensor.function

import tensor.Tensor
import kotlin.math.exp

class Sigmoid(
    private val before: Tensor,
) : Tensor(before = listOf(before), output = 1 / (1 + exp(-before.output))) {
    override fun calcGrad() {
        before.grad += (1 - output) * output * grad
    }
}

fun sigmoid(tensor: Tensor) = Sigmoid(tensor)

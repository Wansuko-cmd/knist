package tensor.function

import tensor.Tensor
import kotlin.math.cos
import kotlin.math.sin

class Sin(
    private val before: Tensor,
) : Tensor(before = listOf(before), output = sin(before.output)) {
    override fun calcGrad() {
        before.grad += grad * cos(before.output)
    }
}

fun sin(tensor: Tensor) = Sin(tensor)

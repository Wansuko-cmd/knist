package tensor

import kotlin.math.E
import kotlin.math.log

class Log(
    private val before: Tensor,
    private val base: Double,
) : Tensor(before = listOf(before), output = log(before.output, base)) {
    override fun calcGrad() {
        before.grad += grad / before.output * log(base, E)
    }
}

fun log(tensor: Tensor, base: Double) = Log(tensor, base)

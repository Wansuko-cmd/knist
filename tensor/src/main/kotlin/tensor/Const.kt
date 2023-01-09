package tensor

class Const(value: Double) : Tensor(before = listOf(), output = value) {
    override fun calcGrad() = Unit
}

fun const(value: Double): Tensor = Const(value)

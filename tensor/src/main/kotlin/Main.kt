package tensor

import kotlin.math.E

fun main() {
    val x1 = const(1.0)
    val x2 = const(2.0)
    val y1 = x1 * x2
    val y2 = log(y1, E)
    val y3 = sin(y1)
    val z = y2 * y3
    println(z.output)
    z.backward()
    println(x2.grad)
}

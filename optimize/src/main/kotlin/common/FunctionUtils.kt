package common

import kotlin.math.exp

fun step(x: Double) = if (x > 0.0) 1.0 else 0.0

fun relu(x: Double) = if (x > 0.0) x else 0.0

fun sigmoid(x: Double) = 1 / (1 + exp(-x))

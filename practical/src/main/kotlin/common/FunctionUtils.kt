@file:Suppress("NOTHING_TO_INLINE")

package common

import kotlin.math.exp

inline fun identity(x: Double) = x

inline fun step(x: Double) = if (x > 0.0) 1.0 else 0.0

inline fun relu(x: Double) = if (x > 0.0) x else 0.0

inline fun sigmoid(x: Double) = 1 / (1 + exp(-x))

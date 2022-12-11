package dataset.signal

import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin
import kotlin.random.Random

fun signal0(t: Double) = (sign(3 * t))
fun signal1(t: Double) = (sin(t))

data class SignalDataset(
    val signal: List<Double>,
    val label: Int,
)

private val signal0Datasets = (1..1000000)
    .map { it.toDouble() / 2.0 }
    .map { signal0(it) }
    .map { it + Random.nextDouble(-0.01, 0.01) }
    .chunked(64)
    .dropLast(1)

private val signal1Datasets = (1..1000000)
    .map { it.toDouble() / 2.0 }
    .map { signal1(it) }
    .map { it + Random.nextDouble(-0.01, 0.01) }
    .chunked(64)
    .dropLast(1)

val signalDatasets = signal0Datasets.map { SignalDataset(signal = it, label = 0) } + signal1Datasets.map { SignalDataset(signal = it, label = 1) }

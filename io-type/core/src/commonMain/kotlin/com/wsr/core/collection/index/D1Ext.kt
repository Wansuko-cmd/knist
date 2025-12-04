package com.wsr.core.collection.index

import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.set
import kotlin.random.Random

fun IOType.D1.topK(k: Int, random: Random = Random): Int {
    val target = maxWithIndex()
        .takeWhile { it.first >= 0 }
        .take(k)
        .toList()

    val sum = target.sumOf { (value, _) -> value.toDouble() }
    if (sum == 0.0) return target.random(random).second
    var p = random.nextDouble(from = 0.0, until = sum)

    for (i in target.indices) {
        p -= target[i].first
        if (p <= 0) return target[i].second
    }
    return target.last().second
}

private fun IOType.D1.maxWithIndex(): Sequence<Pair<Float, Int>> = sequence {
    val target = copyOf()
    repeat(target.size) {
        var index = 0
        var max = Float.MIN_VALUE
        for (i in 0 until shape[0]) {
            if (max < target[i]) {
                index = i
                max = target[i]
            }
        }
        target[index] = Float.MIN_VALUE
        yield(max to index)
    }
}

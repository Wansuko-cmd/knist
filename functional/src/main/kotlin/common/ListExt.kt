package common

fun <T : Comparable<T>> List<T>.maxIndex(): Int =
    this.foldIndexed(null) { index: Int, acc: Pair<Int, T>?, element: T ->
        when {
            acc == null -> index to element
            acc.second > element -> acc
            else -> index to element
        }
    }?.first ?: throw Exception()

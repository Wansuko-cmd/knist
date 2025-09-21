import com.wsr.IOType
import com.wsr.d2.convD1
import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    measureTime {
        createMnistModel(1, 3)
    }.also(::println)
}

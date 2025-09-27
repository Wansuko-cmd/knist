import com.wsr.IOType
import com.wsr.d1.deConvD1
import com.wsr.d2.convD1
import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    measureTime {
        createMnistModel(10, 3)
    }.also(::println)
}

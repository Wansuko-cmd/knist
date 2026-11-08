import dataset.mnist.createMnistModel
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    createMnistModel(epoch = 10)
}

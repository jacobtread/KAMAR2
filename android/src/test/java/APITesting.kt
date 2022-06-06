import com.jacobtread.kamar2.api.KAMAR
import kotlinx.coroutines.runBlocking
import org.junit.Test

class APITesting {

    @Test
    fun globalsTest() {
        runBlocking {

        }
    }

    @Test
    fun test() {
        runBlocking {
            KAMAR.address = ""
            val response = KAMAR.requestGlobals()
            println(response)
        }
    }
}
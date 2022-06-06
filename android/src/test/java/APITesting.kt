import com.jacobtread.kamar2.api.KAMAR
import kotlinx.coroutines.runBlocking
import org.junit.Test

class APITesting {

    private val address = System.getenv("KAMAR_PORTAL")

    init {
        KAMAR.address = address
    }

    @Test
    fun `try request globals`() {
        runBlocking {
            val response = KAMAR.requestGlobals()
            println(response)
        }
    }

}
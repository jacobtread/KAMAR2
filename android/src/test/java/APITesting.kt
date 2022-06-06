import com.jacobtread.kamar2.api.KAMAR
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.text.DateFormat

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

    @Test
    fun `try request settings`() {
        runBlocking {
            val response = KAMAR.requestSettings()
            println(response)
        }
    }

    @Test
    fun `try request notices`() {
        runBlocking {
            val response = KAMAR.requestNotices("06/06/2022")
            println(response)
        }
    }
}
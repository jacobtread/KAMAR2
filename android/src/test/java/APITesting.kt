import com.jacobtread.kamar2.api.KAMAR
import com.jacobtread.kamar2.data.AuthenticationData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.text.DateFormat

class APITesting {

    private val address = System.getenv("KAMAR_PORTAL")
    private val key: String? = System.getenv("KAMAR_KEY")
    private val id: String? = System.getenv("KAMAR_ID")
    private val access: String? = System.getenv("KAMAR_ACCESS")

    init {
        KAMAR.address = address
        if (key != null && id != null && access != null) {
            KAMAR.authData = AuthenticationData(
                key,
                id,
                access.toInt()
            )
        }
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

    @Test
    fun `try request calendar`() {
        runBlocking {
            val response = KAMAR.requestCalendar()
            println(response)
        }
    }

    @Test
    fun `try request timetable`() {
        runBlocking {
            val response = KAMAR.requestStudentTimetable()
            println(response)
        }
    }
}
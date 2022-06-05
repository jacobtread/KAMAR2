import com.jacobtread.kamar2.api.KAMAR
import com.jacobtread.kamar2.utils.getElementByName
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class KAMARTest {

    @Test
    fun test() {
        runBlocking {
            KAMAR.address = ""
            val response = KAMAR.authenticate("", "")
            println(response)
        }
    }
}
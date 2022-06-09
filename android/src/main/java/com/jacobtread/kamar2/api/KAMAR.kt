package com.jacobtread.kamar2.api

import com.jacobtread.kamar2.data.AuthenticationData
import com.jacobtread.kamar2.data.PortalData
import com.jacobtread.kamar2.response.*
import com.jacobtread.kamar2.utils.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

object KAMAR {

    private const val USER_AGENT = "KAMAR/ CFNetwork/ Darwin/"
    private const val DEFAULT_KEY = "vtku"
    private val documentBuilderFactory = DocumentBuilderFactory
        .newInstance()

    private const val NOTICES_DATE_FORMAT = "dd/MM/yyyy"

    var address: String? = null


    var authData = AuthenticationData.DEFAULT
    var portalData = PortalData.DEFAULT

    private val client = HttpClient(Android)


    private fun createApiEndpoint(): String {
        check(address != null) { "Attempted to send an API request without an address" }
        return "https://$address/api/api.php"
    }

    @Throws(RequestException::class, DeserializationException::class)
    suspend fun requestGlobals(): GlobalsResponse {
        val response = requestResource("GetGlobals")
        val definitions = response.getElementsByTagName("PeriodDefinition")
            .arrayTransform { node ->
                val (name, time) = node.getChildrenByNames("PeriodName", "PeriodTime")
                PeriodDefinition(name.textContent, time.textContent)
            }

        val startTimes = response.getElementsByTagName("Day")
            .arrayTransform { dayNode ->
                dayNode.getElementsByTag("PeriodTime")
                    .arrayTransform { periodNode -> periodNode.textContent }
            }

        return GlobalsResponse(definitions, startTimes)
    }

    @Throws(RequestException::class, DeserializationException::class)
    suspend fun requestSettings(): SettingsResponse {
        val response = requestResource("GetSettings")
        val settingsVersion = response.getTextByTag("SettingsVersion")
        val schoolName = response.getTextByTag("SchoolName")
        val logoPath = response.getTextByTag("LogoPath")
        val rawUsers = response.getElementsByTagName("User")
        val userAccess = Array(rawUsers.length) { userIndex ->
            val children = rawUsers[userIndex].childNodes
            val map = HashMap<String, Boolean>()
            children.forEachOfType(Node.ELEMENT_NODE) {
                val name = it.nodeName
                val value = it.textContent
                map[name] = value == "1"
            }
            UserAccess(userIndex, map)
        }
        return SettingsResponse(settingsVersion, schoolName, logoPath, userAccess)
    }

    @Throws(RequestException::class, DeserializationException::class)
    suspend fun requestNotices(date: String): NoticesResponse {
        val response = requestResource("GetNotices", mapOf("Date" to date))
        val meetings = response.getElementsByTagName("Meeting")
            .arrayTransform { node ->
                val level = node.getTextByTag("Level")
                val subject = node.getTextByTag("Subject")
                val body = node.getTextByTag("Body")
                val teacher = node.getTextByTag("Teacher")
                val place = node.getTextByTag("PlaceMeet")
                val dateMeet = node.getTextByTag("DateMeet")
                val time = node.getTextByTag("TimeMeet")
                MeetingNotice(level, subject, body, teacher, place, dateMeet, time)
            }
        val general = response.getElementsByTagName("General")
            .arrayTransform { node ->
                val level = node.getTextByTag("Level")
                val subject = node.getTextByTag("Subject")
                val body = node.getTextByTag("Body")
                val teacher = node.getTextByTag("Teacher")
                GeneralNotice(level, subject, body, teacher)
            }
        return NoticesResponse(meetings, general)
    }

    private fun getCurrentYear(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return year.toString()
    }

    suspend fun requestCalendar(year: String = getCurrentYear()): CalendarResponse {
        val response = requestResource("GetCalendar", mapOf("Year" to year))
        val days = response.getElementsByTagName("Day")
            .arrayTransform { node ->
                val date = node.getTextByTag("Date")
                val status = node.getTextByTag("Status")
                val timetableDay = node.getNumberByTagOrDefault("DayTT", -1)
                val term = node.getNumberByTagOrDefault("Term", -1)
                val termA = node.getNumberByTagOrDefault("TermA", -1)
                val week = node.getNumberByTagOrDefault("Week", -1)
                val weekA = node.getNumberByTagOrDefault("WeekA", -1)
                val weekYear = node.getNumberByTagOrDefault("WeekYear", -1)
                val termYear = node.getNumberByTagOrDefault("TermYear", -1)
                CalendarDay(date, status, timetableDay, term, termA, week, weekA, weekYear, termYear)
            }
        return CalendarResponse(days)
    }

    private fun getCurrentTimetableGrid(): String {
        return getCurrentYear() + "TT"
    }

    suspend fun requestStudentTimetable(grid: String = getCurrentTimetableGrid()): TimetableResponse {
        val response = requestResource("GetStudentTimetable", mapOf(
            "Grid" to grid
        ))
        val students = response.getElementsByTagName("Student")
        val student = students.first {
            val idNumber = it.getTextByTag("IDNumber")
            idNumber == authData.id
        }
        val timetableData = student.getChildByName("TimetableData")
        val weeks = timetableData.childNodes.arrayTransform { weekNode ->
            val days = weekNode.childNodes.arrayTransform { dayNode ->
                val textContent = dayNode.textContent
                val parts = textContent.split('|')
                val periods = if (parts.size == 1) {
                    emptyArray()
                } else {
                    Array(parts.size - 1) { TimetablePeriod.parse(parts[it]) }
                }
                TimetableDay(periods)
            }
            TimetableWeek(days)
        }
        return TimetableResponse(weeks)
    }

    @Throws(AuthenticationException::class, RequestException::class, DeserializationException::class)
    suspend fun authenticate(username: String, password: String): AuthenticationData {
        val response = requestResource(
            "Logon",
            mapOf(
                "Username" to username,
                "Password" to password
            )
        )

        val apiVersion = response.getAttribute("apiversion")
        val portalVersion = response.getAttribute("portalversion")

        val accessLevel = response.getNumberByTag("AccessLevel")

        val errorElement = response.getElementByNameOrNull("Error")
        if (errorElement != null) {
            val errorCode = response.getNumberByTag("ErrorCode")
            throw AuthenticationException(accessLevel, errorElement.text(), errorCode)
        }

        val logonLevel = response.getNumberByTag("LogonLevel")
        val currentStudent = response.getTextByTag("CurrentStudent")
        val key = response.getTextByTag("Key")

        this.authData = AuthenticationData(key, currentStudent, logonLevel)
        this.portalData = PortalData(apiVersion, portalVersion)

        return this.authData
    }

    class RequestException(reason: String) : RuntimeException(reason)

    @Throws(RequestException::class)
    private suspend fun requestResource(command: String, parameters: Map<String, String> = emptyMap()): Element {
        return withContext(Dispatchers.IO) {
            val response = client.submitForm(
                url = createApiEndpoint(), formParameters = Parameters.build {
                    append("Key", authData.key)
                    append("Command", command)
                    parameters.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            ) {
                method = HttpMethod.Post
                header(HttpHeaders.UserAgent, USER_AGENT)
                header("X-Requested-With", "nz.co.KAMAR")
            }
            val rawBody = response.bodyAsText()
            val builder = documentBuilderFactory.newDocumentBuilder()
            val stream = InputSource(StringReader(rawBody))
            try {
                val document = builder.parse(stream)
                return@withContext document.documentElement
            } catch (e: Exception) {
                throw RequestException("Failed to deserialize response")
            }
        }
    }
}

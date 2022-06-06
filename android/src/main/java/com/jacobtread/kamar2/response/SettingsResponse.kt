package com.jacobtread.kamar2.response

data class SettingsResponse(
    val settingsVersion: String,
    val schoolName: String,
    val logoPath: String,
    val userAccess: Array<UserAccess>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SettingsResponse) return false

        if (settingsVersion != other.settingsVersion) return false
        if (schoolName != other.schoolName) return false
        if (logoPath != other.logoPath) return false
        if (!userAccess.contentEquals(other.userAccess)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = settingsVersion.hashCode()
        result = 31 * result + schoolName.hashCode()
        result = 31 * result + logoPath.hashCode()
        result = 31 * result + userAccess.contentHashCode()
        return result
    }
}

data class UserAccess(
    val level: Int,
    val map: Map<String, Boolean>,
) {
    fun hasAccess(fields: AccessField): Boolean = map[fields.key] ?: false
    fun hasAccess(key: String): Boolean = map[key] ?: false
}

enum class AccessField(val key: String) {
    NOTICES("Notices"),
    EVENTS("Events"),
    DETAILS("Details"),
    TIMETABLE("Timetable"),
    ATTENDANCE("Attendance"),
    NCEA("NCEA"),
    Results("Results"),
    GROUPS("Groups"),
    AWARDS("Awards"),
    PASTORAL("Pastoral"),
    REPORT_ABSENCE_PG("ReportAbsencePg"),
    REPORT_ABSENCE("ReportAbsence")
}
package com.jacobtread.kamar2.response

data class CalendarResponse(
    val days: Array<CalendarDay>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CalendarResponse) return false
        if (!days.contentEquals(other.days)) return false
        return true
    }

    override fun hashCode(): Int {
        return days.contentHashCode()
    }
}

data class CalendarDay(
    val date: String,
    val status: String,
    val timetableDay: Int,
    val term: Int,
    val termA: Int,
    val week: Int,
    val weekA: Int,
    val weekYear: Int,
    val termYear: Int
)
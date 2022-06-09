package com.jacobtread.kamar2.response

data class TimetableResponse(val timetableWeeks: Array<TimetableWeek>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimetableResponse) return false
        if (!timetableWeeks.contentEquals(other.timetableWeeks)) return false
        return true
    }

    override fun hashCode(): Int {
        return timetableWeeks.contentHashCode()
    }
}


data class TimetableWeek(val days: Array<TimetableDay>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimetableWeek) return false
        if (!days.contentEquals(other.days)) return false
        return true
    }

    override fun hashCode(): Int {
        return days.contentHashCode()
    }
}

data class TimetableDay(val periods: Array<TimetablePeriod>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimetableDay) return false
        if (!periods.contentEquals(other.periods)) return false
        return true
    }

    override fun hashCode(): Int {
        return periods.contentHashCode()
    }
}

data class TimetablePeriod(
    val type: Char,
    val group: String,
    val subject: String,
    val teacher: String,
    val room: String,
) {
    companion object {
        const val TYPE_CORE = 'C'
        const val TYPE_OPTION = 'O'

        fun parse(value: String): TimetablePeriod {
            val parts = value.split('-')
            println(parts.size)
            check(parts.size == 5) { "Not enough parts to form a period" }
            return TimetablePeriod(
                parts[0].first(),
                parts[1],
                parts[2],
                parts[3],
                parts[4]
            )
        }
    }
}

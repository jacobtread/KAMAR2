package com.jacobtread.kamar2.response

data class NoticesResponse(
    val meetings: Array<MeetingNotice>,
    val generals: Array<GeneralNotice>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoticesResponse) return false

        if (!generals.contentEquals(other.generals)) return false
        if (!meetings.contentEquals(other.meetings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = generals.contentHashCode()
        result = 31 * result + meetings.contentHashCode()
        return result
    }
}


interface Notice {
    val level: String
    val subject: String
    val body: String
    val teacher: String
}

data class MeetingNotice(
    override val level: String,
    override val subject: String,
    override val body: String,
    override val teacher: String,
    val place: String,
    val date: String,
    val time: String,
) : Notice

data class GeneralNotice(
    override val level: String,
    override val subject: String,
    override val body: String,
    override val teacher: String,
) : Notice
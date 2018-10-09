package id.nerdstudio.footballapps.matches.data;

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

open class MatchResponse(val events: List<Match>)

@Parcelize
data class Match(
        @SerializedName("idEvent") var id: String?,
        @SerializedName("idHomeTeam") var homeTeamId: String?,
        @SerializedName("idAwayTeam") var awayTeamId: String?,
        @SerializedName("strHomeTeam") var homeTeam: String?,
        @SerializedName("strAwayTeam") var awayTeam: String?,
        @SerializedName("strHomeFormation") var homeFormation: String?,
        @SerializedName("strAwayFormation") var awayFormation: String?,
        @SerializedName("intHomeScore") var homeScore: Int?,
        @SerializedName("intAwayScore") var awayScore: Int?,
        @SerializedName("intHomeShots") var homeShots: Int?,
        @SerializedName("intAwayShots") var awayShots: Int?,
        @SerializedName("strHomeGoalDetails") var homeGoals: String?,
        @SerializedName("strAwayGoalDetails") var awayGoals: String?,
        @SerializedName("strHomeLineupGoalkeeper") var homeGoalkeeper: String?,
        @SerializedName("strAwayLineupGoalkeeper") var awayGoalkeeper: String?,
        @SerializedName("strHomeLineupDefense") var homeDefender: String?,
        @SerializedName("strAwayLineupDefense") var awayDefender: String?,
        @SerializedName("strHomeLineupMidfield") var homeMidfielder: String?,
        @SerializedName("strAwayLineupMidfield") var awayMidfielder: String?,
        @SerializedName("strHomeLineupForward") var homeForward: String?,
        @SerializedName("strAwayLineupForward") var awayForward: String?,
        @SerializedName("strHomeLineupSubstitutes") var homeSubstitutes: String?,
        @SerializedName("strAwayLineupSubstitutes") var awaySubstitutes: String?,
        @SerializedName("dateEvent") var date: String?,
        @SerializedName("strTime") var time: String?
) : Parcelable {

    @IgnoredOnParcel
    var dateTime: String? = null
        get() {
            if (date != null && time != null) {
                val dateTime = DateTime("${date}T${time}")
                val locale = Locale("in")
                val day = DateTimeFormat.forPattern("EE").withLocale(locale).print(dateTime)
                val date = DateTimeFormat.longDate().withLocale(locale).print(dateTime)
                val time = StringBuilder(DateTimeFormat.longTime().withLocale(locale).print(dateTime)).delete(4, 7)
                return "$day, $date - $time"
            }
            return null
        }

    companion object {
        const val TABLE_FAVORITE_MATCH: String = "TABLE_FAVORITE_MATCH"
        const val ID: String = "_ID"
        const val HOME: String = "HOME_"
        const val AWAY: String = "AWAY_"
        const val TEAM_ID: String = "TEAM$ID"
        const val TEAM_NAME: String = "TEAM_NAME"
        const val TEAM_FORMATION: String = "TEAM_FORMATION"
        const val SCORE: String = "SCORE"
        const val SHOTS: String = "SHOTS"
        const val GOALS: String = "GOALS"
        const val GOALKEEPER: String = "GOALKEEPER"
        const val DEFENDER: String = "DEFENDER"
        const val MIDFIELDER: String = "MIDFIELDER"
        const val FORWARD: String = "FORWARD"
        const val SUBSTITUTES: String = "SUBSTITUTES"
        const val DATE: String = "DATE"
        const val TIME: String = "TIME"
    }
}
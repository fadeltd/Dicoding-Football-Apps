package id.nerdstudio.footballapps.teams.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

open class TeamResponse(val teams: List<Team>)

@Parcelize
data class Team(
        @SerializedName("idTeam") var id: String?,
        @SerializedName("strTeam") var name: String?,
        @SerializedName("strTeamBadge") var teamBadge: String?,
        @SerializedName("strStadium") var stadium: String?,
        @SerializedName("intFormedYear") var formedYear: String?,
        @SerializedName("strDescriptionEN") var description: String?
) : Parcelable {
    companion object {
        const val TABLE_FAVORITE_TEAM: String = "TABLE_FAVORITE_TEAM"
        const val ID: String = "_ID"
        const val NAME: String = "NAME"
        const val BADGE: String = "BADGE"
        const val STADIUM: String = "STADIUM"
        const val FORMED_YEAR: String = "FORMED_YEAR"
        const val DESCRIPTION: String = "DESCRIPTION"

    }
}

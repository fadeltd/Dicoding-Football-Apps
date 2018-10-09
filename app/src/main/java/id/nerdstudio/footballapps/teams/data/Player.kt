package id.nerdstudio.footballapps.teams.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

open class PlayerResponse(val player: List<Player>)

@Parcelize
data class Player(
        @SerializedName("idPlayer") var id: String? = null,
        @SerializedName("idTeam") var idTeam: String? = null,
        @SerializedName("idPlayerManager") var idPlayerManager: String? = null,
        @SerializedName("strNationality") var nationality: String? = null,
        @SerializedName("strPlayer") var name: String? = null,
        @SerializedName("strTeam") var teamName: String? = null,
        @SerializedName("strSport") var sport: String? = null,
        @SerializedName("dateBorn") var dateBorn: Date? = null,
        @SerializedName("dateSigned") var dateSigned: Date? = null,
        @SerializedName("strSigning") var signing: String? = null,
        @SerializedName("strWage") var wage: String? = null,
        @SerializedName("strBirthLocation") var birthLocation: String? = null,
        @SerializedName("strDescriptionEN") var description: String? = null,
        @SerializedName("strGende") var gender: String? = null,
        @SerializedName("strPosition") var position: String? = null,
        @SerializedName("strCollege") var college: String? = null,
        @SerializedName("strFacebook") var facebook: String? = null,
        @SerializedName("strWebsite") var website: String? = null,
        @SerializedName("strTwitter") var twitter: String? = null,
        @SerializedName("strInstagram") var instagram: String? = null,
        @SerializedName("strYoutube") var youtube: String? = null,
        @SerializedName("strHeight") var height: String? = null,
        @SerializedName("strWeight") var weight: String? = null,
        @SerializedName("strThumb") var thumbnail: String? = null,
        @SerializedName("strCutout") var cutout: String? = null,
        @SerializedName("strBanner") var banner: String? = null,
        @SerializedName("strFanart1") var fanart1: String? = null,
        @SerializedName("strFanart2") var fanart2: String? = null,
        @SerializedName("strFanart3") var fanart3: String? = null,
        @SerializedName("strFanart4") var fanart4: String? = null
) : Parcelable
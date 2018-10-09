package id.nerdstudio.footballapps.config

import android.os.Parcelable
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import id.nerdstudio.footballapps.BuildConfig
import kotlinx.android.parcel.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class LeagueResponse(val leagues: List<League>)

@Parcelize
data class League(
        @SerializedName("strLeague") val name: String,
        @SerializedName("idLeague") val id: String,
        var badge: String? = null,
        @SerializedName("strSport") var sport: String? = null
        //, @SerializedName("strLeagueAlternate") var leagueAlternate: String? = null
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

private var leagueList = mutableListOf<League>()
suspend fun getLeagues(): MutableList<League> {
    if (!leagueList.isEmpty()) {
        return leagueList
    } else {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .baseUrl(TheSportsDBApi.getBaseUrl())
                .build()
        val service: Service = retrofit.create(Service::class.java)

        val response = service.getLeagues().await()
        if (response.isSuccessful) {
            leagueList.clear()
            response.body()?.apply {
                leagues.forEach {
                    if (it.sport == "Soccer") {
                        when (it.id) {
                            "4328" -> "${BuildConfig.BASE_URL}${images}xyrpuy1467456595.png"
                            "4331" -> "${BuildConfig.BASE_URL}${images}x5wcpi1517661319.png"
                            "4332" -> "${BuildConfig.BASE_URL}${images}1dqpmt1513798702.png"
                            "4334" -> "${BuildConfig.BASE_URL}${images}8f5jmf1516458074.png"
                            "4335" -> "${BuildConfig.BASE_URL}${images}wxxqxs1474401460.png"
                            "4337" -> "${BuildConfig.BASE_URL}${images}cwo1yp1508666651.png"
                        }
                        leagueList.add(it)
                    }
                }
            }
        } else {
            Log.e("Error", "${response.code()} ${response.message()}")
        }
        return leagueList
    }
}

//data class League(val name: String, val id: String, val badge: String) {
//    override fun toString(): String {
//        return name
//    }
//}

const val images = "images/media/league/badge/small/"

val leagues: MutableList<League> = mutableListOf(
        League("English Premier League", "4328", "${BuildConfig.BASE_URL}${images}xyrpuy1467456595.png", "Soccer"),
        League("German Bundesliga", "4331", "${BuildConfig.BASE_URL}${images}x5wcpi1517661319.png", "Soccer"),
        League("Italian Serie A", "4332", "${BuildConfig.BASE_URL}${images}1dqpmt1513798702.png", "Soccer"),
        League("French Ligue 1", "4334", "${BuildConfig.BASE_URL}${images}8f5jmf1516458074.png", "Soccer"),
        League("Spanish La Liga", "4335", "${BuildConfig.BASE_URL}${images}wxxqxs1474401460.png", "Soccer"),
        League("Dutch Eredivisie", "4337", "${BuildConfig.BASE_URL}${images}cwo1yp1508666651.png", "Soccer")
)
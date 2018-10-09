package id.nerdstudio.footballapps.config

import id.nerdstudio.footballapps.matches.data.MatchResponse
import id.nerdstudio.footballapps.teams.data.PlayerResponse
import id.nerdstudio.footballapps.teams.data.TeamResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET("events{when}league.php")
    fun getMatches(@Path("when") pos: String, @Query("id") id: String): Deferred<Response<MatchResponse>>

    @GET("lookupteam.php")
    fun getTeamDetail(@Query("id") id: String): Deferred<Response<TeamResponse>>

    @GET("search_all_teams.php")
    fun getTeams(@Query("l") league: String): Deferred<Response<TeamResponse>>

    @GET("searchplayers.php")
    fun getPlayers(@Query("t") teamName: String): Deferred<Response<PlayerResponse>>

    @GET("all_leagues.php")
    fun getLeagues(): Deferred<Response<LeagueResponse>>
}
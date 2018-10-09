package id.nerdstudio.footballapps

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import id.nerdstudio.footballapps.config.Service
import id.nerdstudio.footballapps.config.TheSportsDBApi
import id.nerdstudio.footballapps.matches.data.Match
import id.nerdstudio.footballapps.teams.data.Team
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceTest {
    private lateinit var service: Service

    @Before
    internal fun setUp() {
        MockitoAnnotations.initMocks(this);
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(TheSportsDBApi.getBaseUrl())
                .build()
        service = retrofit.create(Service::class.java)
    }

    @Test
    internal fun matchesServiceWithCoroutine() {
        val matches = mutableListOf(Match(
                "526919",
                "133632", "133611",
                "Crystal Palace", "West Brom",
                "", "",
                2, 0,
                5, 1,
                "69':;78':;", "",
                "Wayne Hennessey; ", "Ben Foster; ",
                "Aaron-Wan Bissaka; James Tomkins; Mamadou Sakho; Patrick van Aanholt; ",
                "Allan Nyom; Craig Dawson; Ahmed Hegazy; Kieran Gibbs; ",
                "Luka Milivojevic; James McArthur; Yohan Cabaye; Ruben Loftus-Cheek; ",
                "Matthew Phillips; Jake Livermore; Chris Brunt; James McClean; ",
                "Andros Townsend; Wilfried Zaha; ", "Jay Rodriguez; Jose Salomon Rondon; ",
                "Julian Speroni; Martin Kelly; Pape Ndiaye Souare; Jeffrey Schlupp; Chung-Yong Lee; Jairo Riedewald; Christian Benteke; ",
                "Boaz Myhill; Hal Robson-Kanu; Daniel Sturridge; Oliver Burke; Nacer Chadli; Gareth McAuley; Sam Field; ",
                "2018-05-13", "14:00:00+00:00"
        ))

        runBlocking {
            val repo = service.getMatches("past", "4328").await()
            repo.body()?.apply {
                assert(events[0] == matches[0])
            }
        }
    }

    @Test
    internal fun teamDetailServiceWithCoroutine() {
        // val team = Team("https://www.thesportsdb.com/images/media/team/badge/rytxyw1448813222.png")
        val id = ""
        val name = ""
        val teamBadge = ""
        val stadium = ""
        val formedYear = ""
        val description = ""
        val team = Team(id, name, teamBadge, stadium, formedYear, description)
        runBlocking {
            val repo = service.getTeamDetail("133632").await()
            repo.body()?.apply {
                assertThat(teams[0], `is`(team))
            }
        }
    }
}
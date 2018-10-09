package id.nerdstudio.footballapps.matches

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.R.color.colorAccent
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.R.drawable.ic_star
import id.nerdstudio.footballapps.R.drawable.ic_star_outline
import id.nerdstudio.footballapps.R.id.*
import id.nerdstudio.footballapps.R.menu.menu_detail
import id.nerdstudio.footballapps.adapter.TextViewAdapter
import id.nerdstudio.footballapps.config.Service
import id.nerdstudio.footballapps.config.TheSportsDBApi
import id.nerdstudio.footballapps.matches.data.Match
import id.nerdstudio.footballapps.matches.data.Match.Companion.AWAY
import id.nerdstudio.footballapps.matches.data.Match.Companion.DATE
import id.nerdstudio.footballapps.matches.data.Match.Companion.DEFENDER
import id.nerdstudio.footballapps.matches.data.Match.Companion.FORWARD
import id.nerdstudio.footballapps.matches.data.Match.Companion.GOALKEEPER
import id.nerdstudio.footballapps.matches.data.Match.Companion.GOALS
import id.nerdstudio.footballapps.matches.data.Match.Companion.HOME
import id.nerdstudio.footballapps.matches.data.Match.Companion.ID
import id.nerdstudio.footballapps.matches.data.Match.Companion.MIDFIELDER
import id.nerdstudio.footballapps.matches.data.Match.Companion.SCORE
import id.nerdstudio.footballapps.matches.data.Match.Companion.SHOTS
import id.nerdstudio.footballapps.matches.data.Match.Companion.SUBSTITUTES
import id.nerdstudio.footballapps.matches.data.Match.Companion.TABLE_FAVORITE_MATCH
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_FORMATION
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_ID
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_NAME
import id.nerdstudio.footballapps.matches.data.Match.Companion.TIME
import id.nerdstudio.footballapps.utils.database
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MatchDetailActivity : AppCompatActivity() {
    private var isFavorite = false
    private lateinit var match: Match
    private lateinit var matchDetailUI: MatchDetailUI
    private lateinit var menuItem: Menu
    private var changed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.match_detail)

        match = intent.getParcelableExtra("match")
        matchDetailUI = MatchDetailUI(match)
        matchDetailUI.setContentView(this)

        favoriteState()
    }

    private fun favoriteState() {
        database.use {
            val result = select(TABLE_FAVORITE_MATCH)
                    .whereArgs("(_ID = {id})",
                            "id" to match.id.toString())
            val favorite = result.parseList(classParser<Match>())
            if (!favorite.isEmpty()) isFavorite = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menu_detail, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            add_to_favorite -> {
                if (isFavorite) removeFromFavorite() else addToFavorite()
                isFavorite = !isFavorite
                changed = !changed
                setFavorite()
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onBackPressed() {
        setResult(if (changed) RESULT_OK else RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun addToFavorite() {
        try {
            database.use {
                insert(TABLE_FAVORITE_MATCH,
                        ID to match.id,
                        "$HOME$TEAM_ID" to match.homeTeamId,
                        "$AWAY$TEAM_ID" to match.awayTeamId,
                        "$HOME$TEAM_NAME" to match.homeTeam,
                        "$AWAY$TEAM_NAME" to match.awayTeam,
                        "$HOME$TEAM_FORMATION" to match.homeFormation,
                        "$AWAY$TEAM_FORMATION" to match.awayFormation,
                        "$HOME$SCORE" to match.homeScore,
                        "$AWAY$SCORE" to match.awayScore,
                        "$HOME$SHOTS" to match.homeShots,
                        "$AWAY$SHOTS" to match.awayShots,
                        "$HOME$GOALS" to match.homeGoals,
                        "$AWAY$GOALS" to match.awayGoals,
                        "$HOME$GOALKEEPER" to match.homeGoalkeeper,
                        "$AWAY$GOALKEEPER" to match.awayGoalkeeper,
                        "$HOME$DEFENDER" to match.homeDefender,
                        "$AWAY$DEFENDER" to match.awayDefender,
                        "$HOME$MIDFIELDER" to match.homeMidfielder,
                        "$AWAY$MIDFIELDER" to match.awayMidfielder,
                        "$HOME$FORWARD" to match.homeForward,
                        "$AWAY$FORWARD" to match.awayForward,
                        "$HOME$SUBSTITUTES" to match.homeSubstitutes,
                        "$AWAY$SUBSTITUTES" to match.awaySubstitutes,
                        DATE to match.date, TIME to match.time)
            }
            val message = "${getString(R.string.match)} ${getString(R.string.added_to_favorite)}"
            snackbar(matchDetailUI.root, message).show()
        } catch (e: SQLiteConstraintException) {
            snackbar(matchDetailUI.root, e.localizedMessage).show()
        }
    }

    private fun removeFromFavorite() {
        try {
            database.use {
                delete(TABLE_FAVORITE_MATCH, "(_ID = {id})",
                        "id" to match.id.toString())
            }
            val message = "${getString(R.string.match)} ${getString(R.string.removed_from_favorite)}"
            snackbar(matchDetailUI.root, message).show()
        } catch (e: SQLiteConstraintException) {
            snackbar(matchDetailUI.root, e.localizedMessage).show()
        }
    }

    private fun setFavorite() {
        menuItem.getItem(0)?.icon = ContextCompat.getDrawable(this, if (isFavorite) ic_star else ic_star_outline)
    }
}

class MatchDetailUI(private val match: Match) : AnkoComponent<MatchDetailActivity> {
    lateinit var root: View
    private lateinit var homeScore: TextView
    private lateinit var awayScore: TextView
    private lateinit var homeBadge: ImageView
    private lateinit var awayBadge: ImageView
    private lateinit var homeLoading: ProgressBar
    private lateinit var awayLoading: ProgressBar
    private lateinit var homeTeam: TextView
    private lateinit var awayTeam: TextView
    private lateinit var homeFormation: TextView
    private lateinit var awayFormation: TextView

    override fun createView(ui: AnkoContext<MatchDetailActivity>): View = with(ui) {
        val colorPrimary = ResourcesCompat.getColor(resources, colorPrimary, null)
        scrollView {
            lparams(width = matchParent, height = wrapContent)
            root = verticalLayout {
                lparams(width = matchParent, height = wrapContent)
                padding = dip(16)
                textView {
                    text = match.dateTime
                    textSize = 16F
                    textColor = colorPrimary
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                    verticalMargin = dip(6)
                }
                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    verticalLayout {
                        lparams(width = wrapContent, height = wrapContent)
                        gravity = Gravity.END
                        relativeLayout {
                            lparams(width = wrapContent, height = wrapContent)
                            homeBadge = imageView {
                                id = R.id.home_badge
                            }.lparams(width = dip(75), height = dip(75))
                            homeLoading = progressBar().lparams {
                                centerInParent()
                            }
                            loadTeamDetail(context.getString(R.string.home))
                        }
                        homeTeam = textView {
                            text = match.homeTeam
                            textSize = 16F
                            typeface = Typeface.DEFAULT_BOLD
                            textColor = colorPrimary
                            gravity = Gravity.END
                        }.lparams(width = wrapContent, height = wrapContent) {
                            topMargin = dip(4)
                        }
                        homeFormation = textView {
                            text = match.homeFormation
                            textSize = 16F
                        }.lparams(width = wrapContent, height = wrapContent) {
                            topMargin = dip(4)
                        }
                    }.lparams {
                        topMargin = dip(24)
                        rightMargin = dip(24)
                        leftOf(match_home_score)
                    }

                    homeScore = textView {
                        id = R.id.match_home_score
                        match.homeScore?.apply {
                            text = toString()
                        }
                        textSize = 32F
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(36)
                        leftOf(match_versus)
                    }

                    textView {
                        id = R.id.match_versus
                        text = context.getText(R.string.versus)
                        textSize = 20F
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(44)
                        horizontalMargin = dip(12)
                        centerHorizontally()
                    }

                    awayScore = textView {
                        id = R.id.match_away_score
                        match.awayScore?.apply {
                            text = toString()
                        }
                        textSize = 32F
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(36)
                        rightOf(match_versus)
                    }

                    verticalLayout {
                        lparams(width = wrapContent, height = wrapContent)
                        gravity = Gravity.START
                        relativeLayout {
                            lparams(width = wrapContent, height = wrapContent)
                            awayBadge = imageView {
                                id = R.id.away_badge
                            }.lparams(width = dip(75), height = dip(75))
                            awayLoading = progressBar().lparams {
                                centerInParent()
                            }
                            loadTeamDetail(context.getString(R.string.away))
                        }
                        awayTeam = textView {
                            text = match.awayTeam
                            textSize = 16F
                            typeface = Typeface.DEFAULT_BOLD
                            textColor = colorPrimary
                            gravity = Gravity.START
                        }.lparams(width = wrapContent, height = wrapContent) {
                            topMargin = dip(4)
                        }
                        awayFormation = textView {
                            text = match.awayFormation
                            textSize = 16F
                        }.lparams(width = wrapContent, height = wrapContent) {
                            topMargin = dip(4)
                        }
                    }.lparams {
                        leftMargin = dip(24)
                        topMargin = dip(24)
                        rightOf(match_away_score)
                    }
                }

                view {
                    background = ResourcesCompat.getDrawable(resources, colorAccent, null)
                }.lparams(width = matchParent, height = dip(1))

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeGoals = match.homeGoals?.split(";")
                    homeGoals?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeGoals.toList(), "left")
                        }.lparams(width = wrapContent, height = wrapContent) {
                            alignParentStart()
                        }
                    }

                    textView {
                        text = context.getText(R.string.goals)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awayGoals = match.awayGoals?.split(";")
                    awayGoals?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awayGoals.toList(), "right")
                        }.lparams(width = wrapContent, height = wrapContent) {
                            alignParentEnd()
                        }
                    }
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent) {
                        topMargin = dip(10)
                    }

                    textView {
                        match.homeShots?.apply {
                            text = toString()
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentStart()
                    }

                    textView {
                        id = match_shots
                        text = context.getText(R.string.shots)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    textView {
                        match.awayShots?.apply {
                            text = toString()
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {

                        alignParentEnd()
                    }
                }

                view {
                    background = ResourcesCompat.getDrawable(resources, colorAccent, null)
                }.lparams(width = matchParent, height = dip(1))

                textView {
                    text = context.getString(R.string.lineups)
                    textSize = 16F
                    gravity = Gravity.CENTER_HORIZONTAL
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeGoalkeeper = match.homeGoalkeeper?.split(";")
                    homeGoalkeeper?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeGoalkeeper.toList(), "left")
                        }.lparams(width = matchParent, height = wrapContent) {
                            leftOf(match_goalkeeper)
                            alignParentStart()
                        }
                    }

                    textView {
                        id = match_goalkeeper
                        text = context.getText(R.string.goalkeeper)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awayGoalkeeper = match.awayGoalkeeper?.split(";")
                    awayGoalkeeper?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awayGoalkeeper.toList(), "right")
                        }.lparams(width = matchParent, height = wrapContent) {
                            alignParentEnd()
                        }
                    }
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeDefender = match.homeDefender?.split(";")
                    homeDefender?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeDefender.toList(), "left")
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightOf(match_defender)
                            alignParentStart()
                        }
                    }

                    textView {
                        id = R.id.match_defender
                        text = context.getText(R.string.defense)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awayDefender = match.awayDefender?.split(";")
                    awayDefender?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awayDefender.toList(), "right")
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightOf(match_defender)
                            alignParentEnd()
                        }
                    }
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeMidfielder = match.homeMidfielder?.split(";")
                    homeMidfielder?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeMidfielder.toList(), "left")
                        }.lparams(width = matchParent, height = wrapContent) {
                            leftOf(match_midfielder)
                            alignParentStart()
                        }
                    }

                    textView {
                        id = R.id.match_midfielder
                        text = context.getText(R.string.midfield)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awayMidfielder = match.awayMidfielder?.split(";")
                    awayMidfielder?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awayMidfielder.toList(), "right")
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightOf(match_midfielder)
                            alignParentEnd()
                        }
                    }
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeForward = match.homeForward?.split(";")
                    homeForward?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeForward.toList(), "left")
                        }.lparams(width = match_midfielder, height = wrapContent) {
                            leftOf(match_forward)
                            alignParentStart()
                        }
                    }

                    textView {
                        id = R.id.match_forward
                        text = context.getText(R.string.forward)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awayForward = match.awayForward?.split(";")
                    awayForward?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awayForward.toList(), "right")
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightOf(match_forward)
                            alignParentEnd()
                        }
                    }
                }

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    val homeSubstitutes = match.homeSubstitutes?.split(";")
                    homeSubstitutes?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(homeSubstitutes.toList(), "left")
                        }.lparams(width = matchParent, height = wrapContent) {
                            leftOf(match_substitute)
                            alignParentStart()
                        }
                    }

                    textView {
                        id = R.id.match_substitute
                        text = context.getText(R.string.substitutes)
                        textColor = colorPrimary
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                    }

                    val awaySubstitutes = match.awaySubstitutes?.split(";")
                    awaySubstitutes?.apply {
                        recyclerView {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            adapter = TextViewAdapter(awaySubstitutes.toList(), "right")
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightOf(match_substitute)
                            alignParentEnd()
                        }
                    }
                }

            }
        }
    }

    private fun showLoading(type: String) {
        if (type == "home") homeLoading.visibility = View.VISIBLE else awayLoading.visibility = View.VISIBLE
    }

    private fun hideLoading(type: String) {
        if (type == "home") homeLoading.visibility = View.GONE else awayLoading.visibility = View.GONE
    }

    private fun loadTeamDetail(type: String) {
        showLoading(type)
        val gson = GsonBuilder().create()
        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(TheSportsDBApi.getBaseUrl())
                .build()
        val service: Service = retrofit.create(
                Service::class.java)
        val teamId = if (type == "home") match.homeTeamId else match.awayTeamId

        teamId?.apply {
            launch(UI) {
                try {
                    val request = service.getTeamDetail(teamId)
                    val response = request.await()
                    if (response.isSuccessful) {
                        response.body()?.apply {
                            updateBadge(type, teams[0].teamBadge)
                        }
                    } else {
                        Log.e("Error", "${response.code()} ${response.message()}")
                    }
                } catch (exception: Exception) {
                    hideLoading(type)
                    Log.e("Error", exception.message)
                }
            }
        }
    }

    private fun updateBadge(type: String, teamBadge: String?) {
        teamBadge?.apply {
            Picasso
                    .get()
                    .load(this)
                    .into(if (type == "home") homeBadge else awayBadge) {
                        onSuccess {
                            hideLoading(type)
                        }
                        onError {
                            hideLoading(type)
                        }
                    }
        }
    }
}

class _Callback : Callback {
    private var _onSuccess: (() -> Unit)? = null
    private var _onError: (() -> Unit)? = null

    override fun onSuccess() {
        _onSuccess?.invoke()
    }

    fun onSuccess(func: () -> Unit) {
        _onSuccess = func
    }

    override fun onError(e: Exception?) {
        _onError?.invoke()
    }

    fun onError(func: () -> Unit) {
        _onError = func
    }
}

fun RequestCreator.into(
        target: ImageView,
        func: _Callback.() -> Unit) {
    val callback = _Callback()
    callback.func()
    into(target, callback)
}
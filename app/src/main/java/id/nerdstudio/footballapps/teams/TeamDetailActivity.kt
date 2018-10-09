package id.nerdstudio.footballapps.teams

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.R.drawable.ic_star
import id.nerdstudio.footballapps.R.drawable.ic_star_outline
import id.nerdstudio.footballapps.R.id.add_to_favorite
import id.nerdstudio.footballapps.R.menu.menu_detail
import id.nerdstudio.footballapps.favorites.adapter.FragmentItem
import id.nerdstudio.footballapps.favorites.adapter.PagerAdapter
import id.nerdstudio.footballapps.matches.data.Match.Companion.ID
import id.nerdstudio.footballapps.teams.data.Team
import id.nerdstudio.footballapps.teams.data.Team.Companion.BADGE
import id.nerdstudio.footballapps.teams.data.Team.Companion.DESCRIPTION
import id.nerdstudio.footballapps.teams.data.Team.Companion.FORMED_YEAR
import id.nerdstudio.footballapps.teams.data.Team.Companion.NAME
import id.nerdstudio.footballapps.teams.data.Team.Companion.STADIUM
import id.nerdstudio.footballapps.teams.data.Team.Companion.TABLE_FAVORITE_TEAM
import id.nerdstudio.footballapps.teams.fragment.OverviewFragment
import id.nerdstudio.footballapps.teams.fragment.PlayerFragment
import id.nerdstudio.footballapps.utils.database
import kotlinx.android.synthetic.main.activity_team_detail.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar

class TeamDetailActivity : AppCompatActivity() {
    private var isFavorite = false
    private lateinit var team: Team
    private lateinit var menuItem: Menu
    private lateinit var mPagerAdapter: PagerAdapter
    private val fragments = mutableListOf<FragmentItem>()
    private var changed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)
        setSupportActionBar(toolbar)

        team = intent.getParcelableExtra("team")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        Picasso.get().load(team.teamBadge).into(teamBadge)
        teamName.text = team.name
        teamFormedYear.text = team.formedYear.toString()
        teamStadium.text = team.stadium

        fragments.add(FragmentItem(OverviewFragment.newInstance(team.description
                ?: getString(R.string.no_description)), getString(R.string.overview)))
        fragments.add(FragmentItem(PlayerFragment.newInstance(team.name
                ?: ""), getString(R.string.player)))
        mPagerAdapter = PagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = mPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        favoriteState()
    }

    private fun favoriteState() {
        database.use {
            val result = select(TABLE_FAVORITE_TEAM)
                    .whereArgs("(_ID = {id})",
                            "id" to team.id.toString())
            val favorite = result.parseList(classParser<Team>())
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
                insert(TABLE_FAVORITE_TEAM,
                        ID to team.id,
                        NAME to team.name,
                        BADGE to team.teamBadge,
                        STADIUM to team.stadium,
                        FORMED_YEAR to team.formedYear,
                        DESCRIPTION to team.description
                )
            }
            val message = "${getString(R.string.team)} ${getString(R.string.added_to_favorite)}"
            snackbar(root, message).show()
        } catch (e: SQLiteConstraintException) {
            snackbar(root, e.localizedMessage).show()
        }
    }

    private fun removeFromFavorite() {
        try {
            database.use {
                delete(TABLE_FAVORITE_TEAM, "(_ID = {id})",
                        "id" to team.id.toString())
            }
            val message = "${getString(R.string.team)} ${getString(R.string.removed_from_favorite)}"
            snackbar(root, message).show()
        } catch (e: SQLiteConstraintException) {
            snackbar(root, e.localizedMessage).show()
        }
    }

    private fun setFavorite() {
        menuItem.getItem(0)?.icon = ContextCompat.getDrawable(this, if (isFavorite) ic_star else ic_star_outline)
    }
}
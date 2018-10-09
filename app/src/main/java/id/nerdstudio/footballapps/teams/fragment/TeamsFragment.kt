package id.nerdstudio.footballapps.teams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.R.id.list_team
import id.nerdstudio.footballapps.adapter.SpinnerAdapter
import id.nerdstudio.footballapps.config.League
import id.nerdstudio.footballapps.config.Service
import id.nerdstudio.footballapps.config.TheSportsDBApi
import id.nerdstudio.footballapps.config.leagues
import id.nerdstudio.footballapps.teams.TeamDetailActivity
import id.nerdstudio.footballapps.teams.adapter.TeamAdapter
import id.nerdstudio.footballapps.teams.data.Team
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TeamsFragment : Fragment() {
    private var currentLeague: League? = null
    private lateinit var mAdapter: TeamAdapter
    private lateinit var root: TeamsUI
    private val originalList = mutableListOf<Team>()
    private val currentList = mutableListOf<Team>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = TeamAdapter(currentList) {
            activity?.startActivity(Intent(activity, TeamDetailActivity::class.java)
                    .putExtra("team", it))
        }

        root = TeamsUI(mAdapter, {
            updateCurrentLeague(it)
        }, {
            loadTeamList()
        })

        return root.createView(AnkoContext.create(ctx, this))
    }

    private fun loadTeamList() {
        currentLeague?.apply {
            showLoading()
            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(TheSportsDBApi.getBaseUrl())
                    .build()
            val service: Service = retrofit.create(Service::class.java)

            launch(UI) {
                try {
                    val request = service.getTeams(name)
                    val response = request.await()
                    hideLoading()
                    if (response.isSuccessful) {
                        response.body()?.apply {
                            updateList(teams)
                        }
                    } else {
                        root.swipeRefreshLayout.isRefreshing = false
                        Log.e("Error", "${response.code()} ${response.message()}")
                    }
                } catch (exception: Exception) {
                    hideLoading()
                    clearList()
                    Log.e("Error", exception.message)
                }
            }
        }
    }

    private fun updateCurrentLeague(league: League) {
        if (currentLeague != league) {
            currentLeague = league
            loadTeamList()
        }
    }

    private fun clearList() {
        root.swipeRefreshLayout.isRefreshing = false
        originalList.clear()
        currentList.clear()
        mAdapter.notifyDataSetChanged()
    }

    private fun updateList(data: List<Team>) {
        root.swipeRefreshLayout.isRefreshing = false
        originalList.clear()
        originalList.addAll(data)
        currentList.clear()
        currentList.addAll(originalList)
        mAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        root.mProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        root.mProgressBar.visibility = View.INVISIBLE
    }

    fun search(query: String) {
        currentList.clear()
        if (query.isEmpty()) {
            currentList.addAll(originalList)
        } else {
            for (i in 0 until originalList.size) {
                if (originalList[i].name!!.contains(query, ignoreCase = true)) {
                    currentList.add(originalList[i])
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
}

class TeamsUI(private val mAdapter: TeamAdapter, val updateCurrentLeague: (league: League) -> Unit, val loadTeamList: () -> Unit) : AnkoComponent<TeamsFragment> {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var mProgressBar: ProgressBar
    private lateinit var mSpinner: Spinner

    override fun createView(ui: AnkoContext<TeamsFragment>): View = with(ui) {
        verticalLayout {
            lparams(matchParent, matchParent)

            val spinnerAdapter = SpinnerAdapter(ctx, leagues)
            mSpinner = spinner {
                id = R.id.league_spinner
            }.lparams(matchParent, wrapContent) {
                margin = dip(8)
            }
            mSpinner.adapter = spinnerAdapter
            mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    updateCurrentLeague(leagues[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            swipeRefreshLayout = swipeRefreshLayout {
                setColorSchemeResources(colorPrimary,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                relativeLayout {
                    lparams(matchParent, wrapContent)

                    recyclerView {
                        id = list_team
                        lparams(matchParent, wrapContent)
                        adapter = mAdapter
                        layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    }

                    mProgressBar = progressBar {
                        visibility = View.GONE
                    }.lparams {
                        centerHorizontally()
                    }
                }
                onRefresh {
                    loadTeamList()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}
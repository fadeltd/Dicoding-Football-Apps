package id.nerdstudio.footballapps.matches.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.R.id.list_team
import id.nerdstudio.footballapps.config.League
import id.nerdstudio.footballapps.config.Service
import id.nerdstudio.footballapps.config.TheSportsDBApi
import id.nerdstudio.footballapps.matches.MatchDetailActivity
import id.nerdstudio.footballapps.matches.adapter.MatchAdapter
import id.nerdstudio.footballapps.matches.data.Match
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_POSITION = "position"

class MatchListFragment : Fragment() {
    private var currentLeague: League? = null
    private var originalMatchList: MutableList<Match> = mutableListOf()
    private var currentMatchList: MutableList<Match> = mutableListOf()
    private var position: Int = 0
    private lateinit var mAdapter: MatchAdapter
    private lateinit var root: MatchListUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = MatchAdapter(activity, currentMatchList) {
            activity?.startActivity(Intent(activity, MatchDetailActivity::class.java)
                    .putExtra("match", it))
        }
        root = MatchListUI(position, mAdapter) {
            loadData()
        }

        return root.createView(AnkoContext.create(ctx, this))
    }

    fun loadData() {
        currentLeague?.apply {
            showLoading()
            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(TheSportsDBApi.getBaseUrl())
                    .build()
            val service: Service = retrofit.create(Service::class.java)
            val pos = resources.getString(position)

            launch(UI) {
                try {
                    val request = service.getMatches(pos, id)
                    val response = request.await()
                    hideLoading()
                    if (response.isSuccessful) {
                        response.body()?.apply {
                            updateList(events)
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

    fun updateCurrentLeague(league: League) {
        if (currentLeague != league) {
            currentLeague = league
            loadData()
        }
    }

    private fun clearList() {
        root.swipeRefreshLayout.isRefreshing = false
        originalMatchList.clear()
        currentMatchList.clear()
        mAdapter.notifyDataSetChanged()
    }

    private fun updateList(data: List<Match>) {
        root.swipeRefreshLayout.isRefreshing = false
        originalMatchList.clear()
        originalMatchList.addAll(data)
        currentMatchList.clear()
        currentMatchList.addAll(originalMatchList)
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) =
                MatchListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_POSITION, position)
                    }
                }
    }

    private fun showLoading() {
        root.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        root.progressBar.visibility = View.INVISIBLE
    }

    fun search(query: String) {
        currentMatchList.clear()
        if (query.isEmpty()) {
            currentMatchList.addAll(originalMatchList)
        } else {
            for (i in 0 until originalMatchList.size) {
                if (originalMatchList[i].homeTeam!!.contains(query, ignoreCase = true) || originalMatchList[i].awayTeam!!.contains(query, ignoreCase = true)) {
                    currentMatchList.add(originalMatchList[i])
                }
            }
        }
//        println("${originalMatchList.size} ${currentMatchList.size}")
//        mAdapter.updateList(currentMatchList)
        mAdapter.notifyDataSetChanged()
    }
}

class MatchListUI(private val position: Int, private val mAdapter: MatchAdapter, private val getMatchList: () -> Unit) : AnkoComponent<MatchListFragment> {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar

    override fun createView(ui: AnkoContext<MatchListFragment>): View = with(ui) {
        verticalLayout {
            lparams(width = matchParent, height = matchParent)
            swipeRefreshLayout = swipeRefreshLayout {
                setColorSchemeResources(colorPrimary,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    recyclerView {
                        id = list_team + position.hashCode()
                        lparams(width = matchParent, height = wrapContent)
                        adapter = mAdapter
                        layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    }

                    progressBar = progressBar {
                        visibility = View.GONE
                    }.lparams {
                        centerHorizontally()
                    }
                }
                onRefresh {
                    getMatchList()
                }
            }.lparams(width = matchParent, height = wrapContent)
        }
    }
}
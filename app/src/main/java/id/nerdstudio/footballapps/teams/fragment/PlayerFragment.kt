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
import android.widget.ProgressBar
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.config.Service
import id.nerdstudio.footballapps.config.TheSportsDBApi
import id.nerdstudio.footballapps.teams.PlayerDetailActivity
import id.nerdstudio.footballapps.teams.adapter.PlayerAdapter
import id.nerdstudio.footballapps.teams.data.Player
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ARG_TEAM_NAME = "team_name"

class PlayerFragment : Fragment() {
    private lateinit var mAdapter: PlayerAdapter
    private lateinit var root: PlayerUI
    private lateinit var teamName: String
    private val list = mutableListOf<Player>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teamName = it.getString(ARG_TEAM_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = PlayerAdapter(list) {
            activity?.startActivity(Intent(activity, PlayerDetailActivity::class.java)
                    .putExtra("player", it))
        }

        root = PlayerUI(mAdapter) {
            loadPlayerList()
        }

        return root.createView(AnkoContext.create(ctx, this))
    }

    private fun loadPlayerList() {
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
                val request = service.getPlayers(teamName)
                val response = request.await()
                hideLoading()
                if (response.isSuccessful) {
                    response.body()?.apply {
                        updateList(player)
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

    private fun clearList() {
        root.swipeRefreshLayout.isRefreshing = false
        list.clear()
        mAdapter.notifyDataSetChanged()
    }

    private fun updateList(data: List<Player>) {
        root.swipeRefreshLayout.isRefreshing = false
        list.clear()
        list.addAll(data)
        mAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        root.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        root.progressBar.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(teamName: String) =
                PlayerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TEAM_NAME, teamName)
                    }
                }
    }
}

class PlayerUI(private val mAdapter: PlayerAdapter, val loadPlayerList: () -> Unit) : AnkoComponent<PlayerFragment> {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar

    override fun createView(ui: AnkoContext<PlayerFragment>): View = with(ui) {
        verticalLayout {
            lparams(matchParent, matchParent)

            swipeRefreshLayout = swipeRefreshLayout {
                setColorSchemeResources(colorPrimary,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                relativeLayout {
                    lparams(matchParent, wrapContent)

                    recyclerView {
                        id = View.generateViewId()
                        lparams(matchParent, wrapContent)
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
                    loadPlayerList()
                }
            }.lparams(matchParent, wrapContent)
            loadPlayerList()
        }
    }
}
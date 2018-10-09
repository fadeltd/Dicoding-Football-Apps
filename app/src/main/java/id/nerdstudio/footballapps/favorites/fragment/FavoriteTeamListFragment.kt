package id.nerdstudio.footballapps.favorites.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.R.string.favorites_empty
import id.nerdstudio.footballapps.teams.TeamDetailActivity
import id.nerdstudio.footballapps.teams.adapter.TeamAdapter
import id.nerdstudio.footballapps.teams.data.Team
import id.nerdstudio.footballapps.teams.data.Team.Companion.TABLE_FAVORITE_TEAM
import id.nerdstudio.footballapps.utils.database
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class FavoriteTeamListFragment : Fragment() {
    lateinit var root: FavoriteTeamUI
    lateinit var mAdapter: TeamAdapter
    private var list = mutableListOf<Team>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = TeamAdapter(list) {
            activity?.startActivityForResult(Intent(activity, TeamDetailActivity::class.java)
                    .putExtra("team", it), 100)
        }
        root = FavoriteTeamUI(mAdapter) {
            loadData()
        }

        return root.createView(AnkoContext.create(ctx, this))
    }

    fun loadData() {
        context?.database?.use {
            updateList(select(TABLE_FAVORITE_TEAM).parseList(classParser()))
        }
    }

    private fun clearList() {
        root.swipeRefreshLayout.isRefreshing = false
        list.clear()
        mAdapter.notifyDataSetChanged()
    }

    fun updateList(data: List<Team>) {
        root.swipeRefreshLayout.isRefreshing = false
        list.clear()
        list.addAll(data)
        mAdapter.notifyDataSetChanged()
        if (list.isEmpty()) {
            root.layoutEmpty.visibility = View.VISIBLE
        } else {
            root.layoutEmpty.visibility = View.GONE
        }
    }
}

class FavoriteTeamUI(private val mAdapter: TeamAdapter, private val loadData: () -> Unit) : AnkoComponent<FavoriteTeamListFragment> {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var layoutEmpty: View

    override fun createView(ui: AnkoContext<FavoriteTeamListFragment>): View = with(ui) {
        verticalLayout {
            lparams(matchParent, matchParent)
            swipeRefreshLayout = swipeRefreshLayout {
                setColorSchemeResources(colorPrimary,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                relativeLayout {
                    lparams(matchParent, wrapContent)

                    layoutEmpty = textView {
                        text = resources.getString(favorites_empty)
                    }.lparams(wrapContent, wrapContent) {
                        centerInParent()
                    }

                    recyclerView {
                        id = View.generateViewId()
                        lparams(matchParent, wrapContent)
                        adapter = mAdapter
                        layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    }
                }
                onRefresh {
                    loadData()
                }
            }.lparams(matchParent, wrapContent)

            loadData()
        }
    }
}
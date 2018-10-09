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
import id.nerdstudio.footballapps.matches.MatchDetailActivity
import id.nerdstudio.footballapps.matches.adapter.MatchAdapter
import id.nerdstudio.footballapps.matches.data.Match
import id.nerdstudio.footballapps.matches.data.Match.Companion.TABLE_FAVORITE_MATCH
import id.nerdstudio.footballapps.utils.database
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class FavoriteMatchListFragment : Fragment() {
    lateinit var root: FavoriteListUI
    lateinit var mAdapter: MatchAdapter
    private var list = mutableListOf<Match>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAdapter = MatchAdapter(activity, list) {
            activity?.startActivityForResult(Intent(activity, MatchDetailActivity::class.java)
                    .putExtra("match", it), 100)
        }
        root = FavoriteListUI(mAdapter) {
            loadData()
        }

        return root.createView(AnkoContext.create(ctx, this))
    }

    fun loadData() {
        context?.database?.use {
            updateList(select(TABLE_FAVORITE_MATCH).parseList(classParser()))
        }
    }

    private fun clearList() {
        root.swipeRefreshLayout.isRefreshing = false
        list.clear()
        mAdapter.notifyDataSetChanged()
    }

    fun updateList(data: List<Match>) {
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

class FavoriteListUI(private val mAdapter: MatchAdapter, private val loadData: () -> Unit) : AnkoComponent<FavoriteMatchListFragment> {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var layoutEmpty: View

    override fun createView(ui: AnkoContext<FavoriteMatchListFragment>): View = with(ui) {
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
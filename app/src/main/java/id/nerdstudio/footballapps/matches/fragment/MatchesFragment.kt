package id.nerdstudio.footballapps.matches.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.R.id.league_spinner
import id.nerdstudio.footballapps.adapter.SpinnerAdapter
import id.nerdstudio.footballapps.config.leagues
import id.nerdstudio.footballapps.matches.adapter.MatchPagerAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedTabLayout
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.viewPager

class MatchesFragment : Fragment() {
    private lateinit var root: MatchesUI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = MatchesUI(childFragmentManager)
        return root.createView(AnkoContext.create(ctx, this))
    }

    fun search(query: String) {
        for (i in 0 until root.pagerAdapter.count) {
            val fragment = root.pagerAdapter.instantiateItem(root.mViewPager, i) as MatchListFragment
            fragment.search(query)
        }
    }
}

class MatchesUI(private val fragmentManager: FragmentManager) : AnkoComponent<MatchesFragment> {
    lateinit var pagerAdapter: MatchPagerAdapter
    lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    private lateinit var mSpinner: Spinner

    override fun createView(ui: AnkoContext<MatchesFragment>): View = with(ui) {
        coordinatorLayout {
            lparams(matchParent, matchParent)
            fitsSystemWindows

            // themedAppBarLayout(R.style.AppTheme.AppBarOverlay) {
            appBarLayout {
                lparams(matchParent, wrapContent)
                mTabLayout = themedTabLayout(R.style.ThemeOverlay_AppCompat_Dark) {
                    setSelectedTabIndicatorColor(Color.WHITE)
                }.lparams(matchParent, wrapContent) {

                }
            }

            verticalLayout {
                lparams(matchParent, matchParent)

                val spinnerAdapter = SpinnerAdapter(ctx, leagues)
                mSpinner = spinner {
                    id = league_spinner
                }.lparams(matchParent, wrapContent) {
                    margin = dip(8)
                }
                mSpinner.adapter = spinnerAdapter
                mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        pagerAdapter.currentLeague = leagues[position]
                        pagerAdapter.notifyDataSetChanged()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                pagerAdapter = MatchPagerAdapter(ctx, fragmentManager)
                mViewPager = viewPager {
                    id = View.generateViewId()
                }.lparams(matchParent, matchParent) {
                    // behavior = AppBarLayout.ScrollingViewBehavior()
                }
                mViewPager.adapter = pagerAdapter
                mTabLayout.setupWithViewPager(mViewPager)

            }.lparams {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}


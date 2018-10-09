package id.nerdstudio.footballapps.favorites.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.favorites.adapter.FragmentItem
import id.nerdstudio.footballapps.favorites.adapter.PagerAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedTabLayout
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.viewPager

class FavoritesFragment : Fragment() {
    private val fragments = mutableListOf<FragmentItem>()
    lateinit var root: FavoritesUI
    lateinit var mPagerAdapter: PagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragments.add(FragmentItem(FavoriteMatchListFragment(), resources.getString(R.string.matches)))
        fragments.add(FragmentItem(FavoriteTeamListFragment(), resources.getString(R.string.teams)))

        mPagerAdapter = PagerAdapter(childFragmentManager,  fragments)
        root = FavoritesUI(mPagerAdapter)
        return root.createView(AnkoContext.create(ctx, this))
    }
}

class FavoritesUI(private val mPagerAdapter: PagerAdapter) : AnkoComponent<FavoritesFragment> {
    lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout

    override fun createView(ui: AnkoContext<FavoritesFragment>): View = with(ui) {
        coordinatorLayout {
            lparams(matchParent, matchParent)
            fitsSystemWindows

            appBarLayout {
                lparams(matchParent, wrapContent)
                mTabLayout = themedTabLayout(R.style.ThemeOverlay_AppCompat_Dark) {
                    setSelectedTabIndicatorColor(Color.WHITE)
                }.lparams(matchParent, wrapContent) {

                }
            }

            verticalLayout {
                lparams(matchParent, matchParent)

                mViewPager = viewPager {
                    id = View.generateViewId()
                }.lparams(matchParent, matchParent)
                mViewPager.adapter = mPagerAdapter
                mTabLayout.setupWithViewPager(mViewPager)

            }.lparams {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}


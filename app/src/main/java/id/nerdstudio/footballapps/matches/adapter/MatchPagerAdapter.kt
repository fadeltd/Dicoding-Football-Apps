package id.nerdstudio.footballapps.matches.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import id.nerdstudio.footballapps.R.string.next
import id.nerdstudio.footballapps.R.string.past
import id.nerdstudio.footballapps.config.League
import id.nerdstudio.footballapps.config.leagues
import id.nerdstudio.footballapps.matches.fragment.MatchListFragment

val pos = arrayOf(next, past)

class MatchPagerAdapter(val context: Context, fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

    var currentLeague: League = leagues[0]

    override fun getItem(position: Int): MatchListFragment {
        return MatchListFragment.newInstance(pos[position])
    }

    override fun getCount(): Int {
        return pos.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(pos[position])
    }

    override fun getItemPosition(`object`: Any): Int {
        (`object` as MatchListFragment).updateCurrentLeague(currentLeague)
        return super.getItemPosition(`object`)
    }
}
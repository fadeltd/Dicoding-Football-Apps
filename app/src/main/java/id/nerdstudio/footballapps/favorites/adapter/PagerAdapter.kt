package id.nerdstudio.footballapps.favorites.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

data class FragmentItem(val fragment: Fragment, val title: String)

class PagerAdapter(fragmentManager: FragmentManager, private val fragments: MutableList<FragmentItem>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return fragments[position].fragment
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }
}
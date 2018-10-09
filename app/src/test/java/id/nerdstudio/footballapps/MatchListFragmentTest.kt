package id.nerdstudio.footballapps

import id.nerdstudio.footballapps.matches.fragment.MatchListFragment
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MatchListFragmentTest {
    @Mock
    private val matchListFragment: MatchListFragment? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun loadData() {
        matchListFragment?.loadData()
    }
}


package id.nerdstudio.footballapps.teams.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.nestedScrollView

const val ARG_DESCRIPTION = "desc"

class OverviewFragment : Fragment() {
    lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            description = it.getString(ARG_DESCRIPTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = OverviewUI(description)
        return root.createView(AnkoContext.create(ctx, this))
    }

    companion object {
        @JvmStatic
        fun newInstance(description: String) =
                OverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_DESCRIPTION, description)
                    }
                }
    }
}

class OverviewUI(val description: String) : AnkoComponent<OverviewFragment> {
    override fun createView(ui: AnkoContext<OverviewFragment>): View = with(ui) {
        nestedScrollView {
            lparams(matchParent, wrapContent)
            padding = dip(6)
            textView {
                text = description
            }
        }
    }
}

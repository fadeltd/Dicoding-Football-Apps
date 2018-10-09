package id.nerdstudio.footballapps.matches.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.R.color.colorPrimary
import id.nerdstudio.footballapps.matches.data.Match
import id.nerdstudio.footballapps.utils.addReminderInCalendar
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.joda.time.DateTime
import java.util.*

class MatchAdapter(private val activity: FragmentActivity?, private val list: MutableList<Match>, private val listener: (Match) -> Unit)
    : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemViewUI = ItemViewUI()
        return ViewHolder(itemViewUI.createView(AnkoContext.create(parent.context)), itemViewUI, activity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position], listener)
    }

    override fun getItemCount(): Int = list.size

//    fun updateList(newList: MutableList<Match>) {
//        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newList) { oldPos: Int, newPost: Int ->
//            list[oldPos] == newList[newPost]
//        })
//        diffResult.dispatchUpdatesTo(this)
//    }

    class ViewHolder(itemView: View, private val itemViewUI: ItemViewUI, private val activity: Activity?) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(match: Match, listener: (Match) -> Unit) {
            itemViewUI.date.text = match.dateTime
            itemViewUI.homeTeam.text = match.homeTeam
            if (match.homeScore == null) {
                itemViewUI.homeScore.visibility = View.INVISIBLE
            } else {
                itemViewUI.homeScore.visibility = View.VISIBLE
                itemViewUI.homeScore.text = match.homeScore.toString()
            }
            itemViewUI.awayTeam.text = match.awayTeam
            if (match.awayScore == null) {
                itemViewUI.awayScore.visibility = View.INVISIBLE
            } else {
                itemViewUI.homeScore.visibility = View.VISIBLE
                itemViewUI.awayScore.text = match.awayScore.toString()
            }
            if (match.homeScore == null && match.awayScore == null) {
                itemViewUI.reminder.visibility = View.VISIBLE
                itemViewUI.reminder.setOnClickListener {
                    val calendar = DateTime("${match.date}T${match.time}").toCalendar(Locale("in"))
                    addReminderInCalendar(calendar, activity, match)
                }
            } else {
                itemViewUI.reminder.visibility = View.GONE
            }
            itemView.onClick {
                listener(match)
            }
        }
    }
}

class ItemViewUI : AnkoComponent<Context> {
    lateinit var date: TextView
    lateinit var homeTeam: TextView
    lateinit var homeScore: TextView
    lateinit var awayScore: TextView
    lateinit var awayTeam: TextView
    lateinit var reminder: ImageView

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        return relativeLayout {
            lparams(width = matchParent, height = wrapContent)
            padding = dip(16)
            reminder = imageView {
                isClickable = true
                isFocusable = true
                setImageResource(R.drawable.ic_reminder)
            }.lparams(dip(24), dip(24)) {
                alignParentEnd()
            }

            verticalLayout {
                lparams(width = matchParent, height = wrapContent)

                date = textView {
                    textSize = 14F
                    textColor = ResourcesCompat.getColor(resources, colorPrimary, null)
                }.lparams(width = wrapContent, height = wrapContent) {
                    bottomMargin = dip(20)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    homeTeam = textView {
                        textSize = 14F
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent) {
                        leftOf(R.id.match_home_score)
                        centerVertically()
                    }

                    homeScore = textView {
                        id = R.id.match_home_score
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 22F
                    }.lparams(width = wrapContent, height = wrapContent) {
                        leftOf(R.id.match_versus)
                        centerVertically()
                        leftMargin = dip(8)
                    }

                    textView {
                        id = R.id.match_versus
                        textSize = 14F
                        text = context.getText(R.string.versus)
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerInParent()
                        horizontalMargin = dip(10)
                    }

                    awayScore = textView {
                        id = R.id.match_away_score
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 22F
                    }.lparams(width = wrapContent, height = wrapContent) {
                        rightOf(R.id.match_versus)
                        centerVertically()
                        rightMargin = dip(8)
                    }

                    awayTeam = textView {
                        textSize = 14F
                        gravity = Gravity.START
                    }.lparams(width = wrapContent, height = wrapContent) {
                        rightOf(R.id.match_away_score)
                        centerVertically()
                    }
                }
            }
        }
    }
}

package id.nerdstudio.footballapps.teams.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.teams.data.Player
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class PlayerAdapter(private val players: MutableList<Player>, private val listener: (Player) -> Unit)
    : RecyclerView.Adapter<PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(PlayerUI().createView(AnkoContext.create(parent.context, parent)))
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindItem(players[position], listener)
    }

    override fun getItemCount(): Int = players.size
}

class PlayerUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            linearLayout {
                lparams(matchParent, wrapContent)
                padding = dip(12)
                orientation = LinearLayout.HORIZONTAL

                imageView {
                    id = R.id.avatar
                }.lparams {
                    height = dip(48)
                    width = dip(48)
                }

                relativeLayout {
                    lparams(matchParent, matchParent)
                    gravity = Gravity.CENTER_VERTICAL
                    textView {
                        id = R.id.name
                        textSize = 14f
                        textColor = Color.BLACK
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams {
                        alignParentStart()
                    }

                    textView {
                        id = R.id.position
                        textSize = 14f
                        textColor = Color.GRAY
                    }.lparams {
                        alignParentEnd()
                    }
                }.lparams {
                    leftMargin = dip(6)
                }
            }
        }
    }

}

class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val avatar: ImageView = view.find(R.id.avatar)
    private val name: TextView = view.find(R.id.name)
    private val position: TextView = view.find(R.id.position)

    fun bindItem(player: Player, listener: (Player) -> Unit) {
        Picasso.get().load(player.cutout).into(avatar)
        name.text = player.name
        position.text = player.position
        itemView.onClick { listener(player) }
    }
}
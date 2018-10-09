package id.nerdstudio.footballapps.teams

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import id.nerdstudio.footballapps.R
import id.nerdstudio.footballapps.teams.data.Player
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView

class PlayerDetailActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var root: PlayerDetailUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = intent.getParcelableExtra("player")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = player.name

        root = PlayerDetailUI(player)
        root.setContentView(this)

        if (!player.fanart1.isNullOrEmpty()) {
            Picasso.get().load(player.fanart1).into(root.avatar)
        } else {
            if (!player.cutout.isNullOrEmpty()) {
                Picasso.get().load(player.cutout).into(root.avatar)
            } else {
                root.avatar.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class PlayerDetailUI(val player: Player) : AnkoComponent<PlayerDetailActivity> {
    lateinit var avatar: ImageView
    override fun createView(ui: AnkoContext<PlayerDetailActivity>): View = with(ui) {
        nestedScrollView {
            lparams(width = matchParent)
            verticalLayout {
                lparams(matchParent, matchParent)

                avatar = imageView {}.lparams(matchParent, dip(240))

                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    verticalLayout {
                        gravity = Gravity.CENTER_HORIZONTAL
                        textView {
                            text = ctx.getString(R.string.weight)
                            textSize = 18f
                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        textView {
                            text = player.weight
                            textSize = 24f
                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                    }.lparams(matchParent, matchParent) {
                        weight = 1f
                    }

                    verticalLayout {
                        textView {
                            text = ctx.getString(R.string.height)
                            textSize = 18f
                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        textView {
                            text = player.height?.apply {
                                if (length >= 3) substring(0, 3) else player.height
                            }
                            textSize = 24f
                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                    }.lparams(matchParent, matchParent) {
                        weight = 1f
                    }
                }

                textView {
                    text = player.position
                    textSize = 16f
                }.lparams {
                    topMargin = dip(6)
                    bottomMargin = dip(6)
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                frameLayout {
                    padding = dip(12)
                    textView {
                        text = player.description
                    }
                }
            }
        }
    }
}
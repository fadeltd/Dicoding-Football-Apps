package id.nerdstudio.footballapps.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import id.nerdstudio.footballapps.config.League
import id.nerdstudio.footballapps.config.leagues
import org.jetbrains.anko.*

class SpinnerAdapter(private val context: Context, private val list: List<League>) : BaseAdapter() {
    override fun getItem(position: Int): Any = list.size

    override fun getCount(): Int = list.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = leagues[position]
        val spinnerItem = SpinnerItemUI()
        val view = spinnerItem.createView(AnkoContext.create(context, parent))
        Picasso.get().load(item.badge).into(spinnerItem.icon)
        spinnerItem.name.text = item.name
        return view
    }
}

class SpinnerItemUI : AnkoComponent<ViewGroup> {
    lateinit var icon: ImageView
    lateinit var name: TextView

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        linearLayout {
            lparams(width = matchParent, height = wrapContent)
            padding = dip(5)
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            icon = imageView {
            }.lparams(width = dip(20), height = dip(20))
            name = textView {
                textSize = 14F
                typeface = Typeface.DEFAULT_BOLD
                textColor = Color.BLACK
                gravity = Gravity.CENTER_VERTICAL
            }.lparams(width = wrapContent, height = wrapContent) {
                leftMargin = dip(10)
            }
        }
    }

}
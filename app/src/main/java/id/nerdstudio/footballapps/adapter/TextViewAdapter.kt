package id.nerdstudio.footballapps.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*

class TextViewAdapter(private val list: List<String>, val position: String)
    : RecyclerView.Adapter<TextViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textViewUI = TextViewUI(position)
        return ViewHolder(textViewUI.createView(AnkoContext.create(parent.context)), textViewUI)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View, private val textViewUI: TextViewUI) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(text: String) {
            val split = text.split(":")
            textViewUI.text.text = if (split.size > 1) {
                "${split[1]} ${split[0]}".trim()
            } else {
                text.trim()
            }
        }
    }
}


class TextViewUI(val position: String) : AnkoComponent<Context> {
    lateinit var text: TextView

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        return relativeLayout {
            lparams(width = matchParent, height = wrapContent)
            gravity = if (position == "right") Gravity.END else Gravity.START
            text = textView {
                maxLines = 1
            }.lparams(width = wrapContent, height = wrapContent)
        }
    }
}
package id.nerdstudio.footballapps.utils

import android.support.annotation.Nullable
import android.support.v7.util.DiffUtil

class DiffCallback<T>(private val oldList: MutableList<T>, private val newList: MutableList<T>, private val areItemsTheSame: (oldPos: Int, newPos: Int) -> Boolean) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame.invoke(oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}
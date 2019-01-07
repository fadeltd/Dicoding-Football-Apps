package id.nerdstudio.footballapps.matches.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(val name: String): Parcelable

@Parcelize
data class EventResponse(
        @field:SerializedName("events", alternate = ["event"]) val events: List<Event>? = null
): Parcelable

@Parcelize
data class ListResponse<out T: Parcelable>(
        @field:SerializedName("contents", alternate = ["event", "event"]) val contents: List<T>? = null
): Parcelable {
    constructor(): this(null)
}

fun fetchData(){
    val data = ListResponse<Event>()
}

package id.nerdstudio.footballapps.utils

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import id.nerdstudio.footballapps.matches.data.Match
import java.util.*

fun addMatchReminder(context: Activity?, calendar: Calendar, match: Match){
    val title = "${match.homeTeam} vs ${match.awayTeam}"
    val description = "Match between $title"
    addMatchReminder(context, calendar, title, description)
}

fun addMatchReminder(context: Activity?, calendar: Calendar, title: String, description: String){
    context?.run {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(CalendarContract.Events.TITLE, title)
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, description)
        intent.putExtra(CalendarContract.CalendarAlerts.ALARM_TIME, calendar.timeInMillis)
        startActivity(intent)
    }
}

fun createEvent(calendar: Calendar, title: String, description: String) : ContentValues{
    /** Inserting an event in calendar.  */
    val event = ContentValues()
    event.put(CalendarContract.Events.CALENDAR_ID, 3)
    event.put(CalendarContract.Events.TITLE, title)
    event.put(CalendarContract.Events.DESCRIPTION, description)
    event.put(CalendarContract.Events.ALL_DAY, 0)
    event.put(CalendarContract.Events.DTSTART, calendar.timeInMillis)
    event.put(CalendarContract.Events.DTEND, calendar.timeInMillis + 120 * 60 * 1000)
    event.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE)
    event.put(CalendarContract.Events.HAS_ALARM, 1)
    return event
}

fun addReminderInCalendar(calendar: Calendar, context: Activity?, match: Match) {
    context?.run {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR), 100)
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            val cr = contentResolver
            val title = "${match.homeTeam} vs ${match.awayTeam}"
            val description = "Match between $title"
            val event = createEvent(calendar, title, description)
            val eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, event)

            /** Adding reminder for event added.  */
            val reminder = ContentValues()
            reminder.put(CalendarContract.Reminders.EVENT_ID, eventUri.lastPathSegment.toLong())
            reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            reminder.put(CalendarContract.Reminders.MINUTES, 0)
            try {
                val remindersUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder)
                val added = remindersUri.lastPathSegment.toInt()

                if (added > 0) {
                    val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
                    ContentUris.appendId(builder, calendar.timeInMillis)
                    val calendarIntent = Intent(Intent.ACTION_VIEW).setData(builder.build())
                    calendarIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(calendarIntent)

                    val eventIntent = Intent(Intent.ACTION_VIEW).setData(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventUri.lastPathSegment.toLong()))
                    eventIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(eventIntent)
                }
            } catch (ex: Exception) {
                Toast.makeText(context, ex.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
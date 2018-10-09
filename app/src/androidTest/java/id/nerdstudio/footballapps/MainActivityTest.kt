package id.nerdstudio.footballapps

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import id.nerdstudio.footballapps.R.id.*
import id.nerdstudio.footballapps.R.string.added_to_favorite
import id.nerdstudio.footballapps.R.string.removed_from_favorite
import org.hamcrest.core.StringContains
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun tesFavoriteBehaviour() {
        onView(withId(league_spinner)).check(matches(isDisplayed()))
        onView(withId(league_spinner)).perform(click())
        onView(withText("Spanish La Liga")).perform(click())

        Thread.sleep(4000)
        onView(withText(StringContains("Barcelona"))).check(matches(isDisplayed()))
        onView(withText(StringContains("Barcelona"))).perform(click())

        Thread.sleep(2000)
        onView(withId(add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(add_to_favorite)).perform(click())
        onView(withText(added_to_favorite)).check(matches(isDisplayed()))
        pressBack()

        Thread.sleep(2000)
        onView(withText("Favorites")).check(matches(isDisplayed()))
        onView(withText("Favorites")).perform(click())
        onView(withId(list_team + "Favorites".hashCode())).check(matches(isDisplayed()))
        onView(withId(list_team + "Favorites".hashCode()))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(add_to_favorite)).perform(click())
        onView(withText(removed_from_favorite)).check(matches(isDisplayed()))
        pressBack()
    }

    @Test
    fun testRecyclerViewBehaviour() {
        Thread.sleep(4000)
        onView(withId(list_team + "Prev".hashCode())).check(matches(isDisplayed()))
        onView(withId(list_team + "Prev".hashCode())).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
        onView(withId(list_team + "Prev".hashCode())).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(5, click()))
    }
}
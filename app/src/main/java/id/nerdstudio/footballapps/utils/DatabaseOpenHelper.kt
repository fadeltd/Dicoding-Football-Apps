package id.nerdstudio.footballapps.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import id.nerdstudio.footballapps.matches.data.Match.Companion.AWAY
import id.nerdstudio.footballapps.matches.data.Match.Companion.DATE
import id.nerdstudio.footballapps.matches.data.Match.Companion.DEFENDER
import id.nerdstudio.footballapps.matches.data.Match.Companion.FORWARD
import id.nerdstudio.footballapps.matches.data.Match.Companion.GOALKEEPER
import id.nerdstudio.footballapps.matches.data.Match.Companion.GOALS
import id.nerdstudio.footballapps.matches.data.Match.Companion.HOME
import id.nerdstudio.footballapps.matches.data.Match.Companion.ID
import id.nerdstudio.footballapps.matches.data.Match.Companion.MIDFIELDER
import id.nerdstudio.footballapps.matches.data.Match.Companion.SCORE
import id.nerdstudio.footballapps.matches.data.Match.Companion.SHOTS
import id.nerdstudio.footballapps.matches.data.Match.Companion.SUBSTITUTES
import id.nerdstudio.footballapps.matches.data.Match.Companion.TABLE_FAVORITE_MATCH
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_FORMATION
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_ID
import id.nerdstudio.footballapps.matches.data.Match.Companion.TEAM_NAME
import id.nerdstudio.footballapps.matches.data.Match.Companion.TIME
import id.nerdstudio.footballapps.teams.data.Team.Companion.BADGE
import id.nerdstudio.footballapps.teams.data.Team.Companion.DESCRIPTION
import id.nerdstudio.footballapps.teams.data.Team.Companion.FORMED_YEAR
import id.nerdstudio.footballapps.teams.data.Team.Companion.NAME
import id.nerdstudio.footballapps.teams.data.Team.Companion.STADIUM
import id.nerdstudio.footballapps.teams.data.Team.Companion.TABLE_FAVORITE_TEAM

import org.jetbrains.anko.db.*

class DatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Favorite.db", null, 1) {
    companion object {
        private var instance: DatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseOpenHelper {
            if (instance == null) {
                instance = DatabaseOpenHelper(ctx)
            }
            return instance as DatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE_FAVORITE_MATCH, true,
                ID to TEXT + PRIMARY_KEY,
                "$HOME$TEAM_ID" to TEXT, "$AWAY$TEAM_ID" to TEXT,
                "$HOME$TEAM_NAME" to TEXT, "$AWAY$TEAM_NAME" to TEXT,
                "$HOME$TEAM_FORMATION" to TEXT, "$AWAY$TEAM_FORMATION" to TEXT,
                "$HOME$SCORE" to INTEGER, "$AWAY$SCORE" to INTEGER,
                "$HOME$SHOTS" to INTEGER, "$AWAY$SHOTS" to INTEGER,
                "$HOME$GOALS" to TEXT, "$AWAY$GOALS" to TEXT,
                "$HOME$GOALKEEPER" to TEXT, "$AWAY$GOALKEEPER" to TEXT,
                "$HOME$DEFENDER" to TEXT, "$AWAY$DEFENDER" to TEXT,
                "$HOME$MIDFIELDER" to TEXT, "$AWAY$MIDFIELDER" to TEXT,
                "$HOME$FORWARD" to TEXT, "$AWAY$FORWARD" to TEXT,
                "$HOME$SUBSTITUTES" to TEXT, "$AWAY$SUBSTITUTES" to TEXT,
                DATE to TEXT, TIME to TEXT
        )
        db.createTable(TABLE_FAVORITE_TEAM, true,
                ID to TEXT + PRIMARY_KEY,
                NAME to TEXT,
                BADGE to TEXT,
                STADIUM to TEXT,
                FORMED_YEAR to TEXT,
                DESCRIPTION to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(TABLE_FAVORITE_MATCH, true)
        db.dropTable(TABLE_FAVORITE_TEAM, true)
    }
}

val Context.database: DatabaseOpenHelper
    get() = DatabaseOpenHelper.getInstance(this)
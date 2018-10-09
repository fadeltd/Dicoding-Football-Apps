package id.nerdstudio.footballapps.config

import id.nerdstudio.footballapps.BuildConfig

object TheSportsDBApi {
    fun getBaseUrl(): String {
        return "${BuildConfig.BASE_URL}api/v1/json/${BuildConfig.TSDB_API_KEY}/"
    }
}
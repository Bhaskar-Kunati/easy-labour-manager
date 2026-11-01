package com.easylabour.app.data.remote

import android.content.Context
import com.easylabour.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlProvider @Inject constructor(@ApplicationContext private val context: Context) {
    private val prefs = context.getSharedPreferences("network_prefs", Context.MODE_PRIVATE)

    fun getBaseUrl(): String {
        var url = prefs.getString("base_url", BuildConfig.API_BASE_URL) ?: BuildConfig.API_BASE_URL
        if (!url.endsWith("/")) {
            url += "/"
        }
        return url
    }

    fun setBaseUrl(url: String) {
        prefs.edit().putString("base_url", url).apply()
    }

    fun resetToDefault() {
        prefs.edit().remove("base_url").apply()
    }
}

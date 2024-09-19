package com.paytondeveloper.sharedqandroid

import android.app.Application
import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient

object AppInfo {
    lateinit var application: Application
    var httpClient = OkHttpClient.Builder().cache(null).build()
    val onboardingPage: OnboardingPage
        get() {
            var prefs = application.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            var rawOnboardingPage = prefs.getString("page", "auth")
            Log.d("onboardingpage", rawOnboardingPage.toString())
            return when (rawOnboardingPage) {
                null -> OnboardingPage.Auth
                "auth" -> OnboardingPage.Auth
                "musicservice" -> OnboardingPage.MusicService
                "tutorial" -> OnboardingPage.HowItWorks
                "home" -> OnboardingPage.Complete
                else -> OnboardingPage.Auth
            }
        }
    fun setOnboardingPage(page: OnboardingPage) {
        var prefs = application.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        prefs.edit().putString("page", page.route).apply()
    }
    lateinit var mainActivity: MainActivity
}

enum class OnboardingPage(val route: String) {
    Auth("auth"),
    MusicService("musicservice"),
    HowItWorks("tutorial"),
    Complete("home")
}
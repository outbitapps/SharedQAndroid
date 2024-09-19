package com.paytondeveloper.sharedqandroid.musicservices

import android.content.Context
import android.content.Intent
import com.paytondeveloper.sharedqandroid.protocol.SQSong
import com.apple.android.sdk.authentication.AuthenticationFactory
import com.apple.android.sdk.authentication.AuthenticationManager
import com.apple.android.sdk.authentication.TokenResult
import com.paytondeveloper.sharedqandroid.AppInfo
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Date

class AppleMusicService: MusicService {
    private val json = Json { ignoreUnknownKeys = true }
    private var authenticationManager: AuthenticationManager = AuthenticationFactory.createAuthenticationManager(
        AppInfo.application.applicationContext)
    private val devToken = "eyJhbGciOiJFUzI1NiIsImtpZCI6IldRMjRWOVozNjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJCUjkyNk5ZOUZTIiwiZXhwIjoxNzQwMzczMzQ5LCJpYXQiOjE3MjUyNDk3NDl9.eui6rCeSsHYagtnHhvClTWsbmOMhCzwlO-Fx3kgGfj5l4XjfSQQwrGXhBw-EoRYmF6Sv5Ehqu6tmAvlgNkOriw"
    fun openAuthLink(): Intent {
        val intent = authenticationManager.createIntentBuilder(devToken)
            .setStartScreenMessage("Connect SharedQ to Apple Music")
            .setHideStartScreen(true)
            .build()
        return intent
    }
    fun handleAuthIntent(intent: Intent): TokenResult {
        return authenticationManager.handleTokenResult(intent)
    }

    fun signOut() {
        val prefs = AppInfo.application.applicationContext.getSharedPreferences("keys", Context.MODE_PRIVATE)
        prefs.edit().remove("amkey").apply()
        var services = prefs.getStringSet("musicservices", setOf())?.toSet()
        var newServices = services?.minus("apple")
        prefs.edit().putStringSet("musicservices", newServices).apply()
    }

    fun storeToken(token: String) {
        val sharedPrefs = AppInfo.application.applicationContext.getSharedPreferences("keys", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("amkey", token).apply()
//        AppInfo.addServiceToPrefs("apple")
    }

    companion object {
        var shared = AppleMusicService()
    }

    fun getToken(): String? {
        val sharedPrefs = AppInfo.application.applicationContext.getSharedPreferences("keys", Context.MODE_PRIVATE)
        return sharedPrefs.getString("amkey", "null")
    }
    override suspend fun getMostRecentSong(): SQSong {
        TODO("Not yet implemented")
    }

    override suspend fun recentlyPlayed(): List<SQSong> {
        TODO("Not yet implemented")
    }

    override suspend fun playSong(song: SQSong) {
        TODO("Not yet implemented")
    }

    override suspend fun playAt(timestamp: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun getSongTimestamp(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun stopPlayback() {
        TODO("Not yet implemented")
    }

    override suspend fun nextSong() {
        TODO("Not yet implemented")
    }

    override suspend fun addQueue(queue: List<SQSong>) {
        TODO("Not yet implemented")
    }

    override suspend fun pauseSong() {
        TODO("Not yet implemented")
    }

    override suspend fun prevSong() {
        TODO("Not yet implemented")
    }

    override suspend fun seekTo(timestamp: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun searchFor(query: String): List<SQSong>? {
        TODO("Not yet implemented")
    }

    override suspend fun registerStateListeners() {
        TODO("Not yet implemented")
    }
}
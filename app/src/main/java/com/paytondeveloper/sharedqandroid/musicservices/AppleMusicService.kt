package com.paytondeveloper.sharedqandroid.musicservices

import android.content.Context
import android.content.Intent
import android.media.browse.MediaBrowser.MediaItem
import android.media.session.MediaSession
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import android.util.Log
import com.apple.android.music.playback.controller.MediaPlayerController
import com.apple.android.music.playback.controller.MediaPlayerControllerFactory
import com.apple.android.music.playback.model.MediaItemType
import com.apple.android.music.playback.model.MediaPlayerException
import com.apple.android.music.playback.model.PlaybackState
import com.apple.android.music.playback.model.PlayerQueueItem
import com.apple.android.music.playback.queue.CatalogPlaybackQueueItemProvider
import com.apple.android.music.playback.queue.PlaybackQueueInsertionType
import com.apple.android.sdk.authentication.AuthenticationFactory
import com.apple.android.sdk.authentication.AuthenticationManager
import com.apple.android.sdk.authentication.TokenProvider
import com.apple.android.sdk.authentication.TokenResult
import com.paytondeveloper.sharedqandroid.AppInfo
import com.paytondeveloper.sharedqandroid.protocol.SQSong
import com.paytondeveloper.sharedqandroid.sync.SQManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Request


class AppleMusicService: MusicService, Handler.Callback, TokenProvider, MediaPlayerController.Listener, MediaSession.Callback() {
    private val json = Json { ignoreUnknownKeys = true }
    private var authenticationManager: AuthenticationManager = AuthenticationFactory.createAuthenticationManager(
        AppInfo.application.applicationContext)
    private val devToken = "eyJhbGciOiJFUzI1NiIsImtpZCI6IldRMjRWOVozNjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJCUjkyNk5ZOUZTIiwiZXhwIjoxNzQwMzczMzQ5LCJpYXQiOjE3MjUyNDk3NDl9.eui6rCeSsHYagtnHhvClTWsbmOMhCzwlO-Fx3kgGfj5l4XjfSQQwrGXhBw-EoRYmF6Sv5Ehqu6tmAvlgNkOriw"
    lateinit var playerController: MediaPlayerController
    lateinit var mediaSession: MediaSession

    init {
//        mediaSession = MediaSessionCompat(this, TAG)
//        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
//        mediaSession.setCallback(
//            MediaSessionManager(
//                this,
//                serviceHandler,
//                playerController,
//                mediaSession
//            ), serviceHandler
//        )
//        setSessionToken(mediaSession.getSessionToken())

//        mediaProvider = LocalMediaProvider(this)

        val serviceHandlerThread = object : HandlerThread("MediaPlaybackService:Handler", Process.THREAD_PRIORITY_BACKGROUND) {
            override fun onLooperPrepared() {
                val serviceHandler = Handler(looper, this@AppleMusicService)
                // Now you can safely use serviceHandler here
                playerController = MediaPlayerControllerFactory.createLocalController(
                    AppInfo.application.applicationContext,
                    serviceHandler,
                    this@AppleMusicService
                )
                Log.d("looper", "looper ready - pc init")
                playerController.addListener(this@AppleMusicService)
                mediaSession = MediaSession(AppInfo.application.applicationContext, "SQAM")
                mediaSession.setCallback(this@AppleMusicService)
            }
        }
        serviceHandlerThread.start()
    }

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
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getMostRecentSong(): SQSong {
        TODO("Not yet implemented")


    }

    override suspend fun recentlyPlayed(): List<SQSong> {
        val token = getToken() ?: "fuck"
        var request = Request.Builder()
            .url("https://api.music.apple.com/v1/me/recent/played?limit=10")
            .addHeader("Authorization", "Bearer ${devToken}")
            .addHeader("Music-User-Token", token)
            .build()
        val res = AppInfo.httpClient.newCall(request).execute()
        res.body?.let {
            try {
                val response = json.decodeFromStream<PaginatedResourceCollectionResponse>(res.body!!.byteStream())
                for (resource in response.data) {
                    Log.d("amgmrs", resource.href)
                }
            } catch (e:Exception) {
                Log.e("amgmrs", "Error getting most recent songs: ${e}")
            }
        }
        return listOf()
    }

    override suspend fun playSong(song: SQSong) {
        Log.d("playsong", "playsong")
        var musicId = getMusicIdFromSong(song)
        val builder = CatalogPlaybackQueueItemProvider.Builder()
        builder.items(MediaItemType.SONG, musicId)
        playerController.prepare(builder.build(), true)
        Log.d("playsong", "musicid: ${musicId}")

    }
    suspend fun getMusicIdFromSong(song: SQSong): String {
        val token = getToken() ?: "fuck"
        var request = Request.Builder()
            .url("https://api.music.apple.com/v1/catalog/us/songs?filter[isrc]=${song.isrc}")
            .addHeader("Authorization", "Bearer ${devToken}")
            .addHeader("Music-User-Token", token)
            .build()
        val res = AppInfo.httpClient.newCall(request).execute()
        res.body?.let {
            try {
                val response = json.decodeFromStream<SongsResponse>(res.body!!.byteStream())
                for (resource in response.data) {
                    Log.d("amgmrs", resource.attributes.name)
                    return@getMusicIdFromSong resource.id
                }
            } catch (e:Exception) {
                Log.e("amgmrs", "Error getting most recent songs: ${e}")
            }
        }
        Log.d("amgmrs", "${res.code} ${res.message}")
        return ""
    }

    override suspend fun playAt(timestamp: Double) {
        playerController.seekToPosition(timestamp.toLong())
    }

    override suspend fun getSongTimestamp(): Double {
        val position = playerController.currentPosition
        Log.d("tsupdate", position.toString())
        return position.toDouble()
    }


    override suspend fun stopPlayback() {
        playerController.stop()
    }

    override suspend fun nextSong() {
        playerController.skipToNextItem()
    }

    override suspend fun clearQueue() {
        for (item in playerController.queueItems) {
            playerController.removeQueueItemWithId(item.playbackQueueId)
        }
    }

    override suspend fun addQueue(queue: List<SQSong>) {
       for (song in queue) {
           var musicId = getMusicIdFromSong(song)
           val builder = CatalogPlaybackQueueItemProvider.Builder()
           builder.items(MediaItemType.SONG, musicId)
           playerController.addQueueItems(builder.build(), PlaybackQueueInsertionType.INSERTION_TYPE_AT_END)
       }
    }

    override suspend fun pauseSong() {
        playerController.pause()
    }

    override suspend fun prevSong() {
        playerController.skipToPreviousItem()
    }

    override suspend fun seekTo(timestamp: Double) {
        playerController.seekToPosition(timestamp.toLong())
    }

    override suspend fun searchFor(query: String): List<SQSong>? {
        TODO("Not yet implemented")
    }

    override suspend fun registerStateListeners() {
        playerController.addListener(this)
    }

    override fun getDeveloperToken(): String {
        return devToken
    }

    override fun getUserToken(): String {
        return getToken() ?: ""
    }

    override fun handleMessage(msg: Message): Boolean {
        Log.d("msg", msg.toString())
        return true
    }

    override fun onPlayerStateRestored(p0: MediaPlayerController) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackStateChanged(p0: MediaPlayerController, p1: Int, p2: Int) {
//        TODO("Not yet implemented")
        when (p2) {
            PlaybackState.PLAYING -> {
                if (p1 == PlaybackState.PAUSED) {
                    SQManager.shared.syncManager.playSong()
                }
            }
            PlaybackState.PAUSED -> {
                if (p1 == PlaybackState.PLAYING) {
                    SQManager.shared.syncManager.pauseSong()
                }
            }
            PlaybackState.STOPPED -> {
//                SQManager.shared.syncManager.disconnect()
            }
        }
    }

    override fun onPlaybackStateUpdated(p0: MediaPlayerController) {
//        TODO("Not yet implemented")
    }

    override fun onBufferingStateChanged(p0: MediaPlayerController, p1: Boolean) {
//        TODO("Not yet implemented")
    }

    override fun onCurrentItemChanged(
        p0: MediaPlayerController,
        p1: PlayerQueueItem?,
        p2: PlayerQueueItem?
    ) {
//        TODO("Not yet implemented")
    }

    override fun onItemEnded(p0: MediaPlayerController, p1: PlayerQueueItem, p2: Long) {
//        TODO("Not yet implemented")
    }

    override fun onMetadataUpdated(p0: MediaPlayerController, p1: PlayerQueueItem) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackQueueChanged(
        p0: MediaPlayerController,
        p1: MutableList<PlayerQueueItem>
    ) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackQueueItemsAdded(p0: MediaPlayerController, p1: Int, p2: Int, p3: Int) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackError(p0: MediaPlayerController, p1: MediaPlayerException) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackRepeatModeChanged(p0: MediaPlayerController, p1: Int) {
//        TODO("Not yet implemented")
    }

    override fun onPlaybackShuffleModeChanged(p0: MediaPlayerController, p1: Int) {
//        TODO("Not yet implemented")
    }
}
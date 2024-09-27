package com.paytondeveloper.sharedqandroid.sync

import android.util.Base64
import android.util.Log
import com.paytondeveloper.sharedqandroid.AppInfo
import com.paytondeveloper.sharedqandroid.protocol.PlayPauseState
import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.protocol.SQQueueItem
import com.paytondeveloper.sharedqandroid.protocol.SQSong
import com.paytondeveloper.sharedqandroid.protocol.SQUser
import com.paytondeveloper.sharedqandroid.protocol.WSMessage
import com.paytondeveloper.sharedqandroid.protocol.WSMessageType
import com.paytondeveloper.sharedqandroid.protocol.WSTimestampUpdate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.Date

class SyncManager(serverURL: String, websocketURL: String): WebSocketListener() {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    public var delegate: SyncDelegate? = null
    public var serverURL: String
    public var websocketURL: String
    lateinit var group2: SQGroup
    lateinit var webSocket: WebSocket
    init {
        this.serverURL = serverURL
        this.websocketURL = websocketURL
    }
    suspend fun connectToGroup(group: SQGroup, token: String) {
        Log.d("connectotgroup", websocketURL)
        val tokenURL = "${serverURL}/groups/getws/${group.id}"
        val tokenRequest = Request.Builder()
            .url(tokenURL)
            .addHeader("Authorization", "Bearer ${token}")
            .build()
        val res = AppInfo.httpClient.newCall(tokenRequest).execute()
        res.body?.let {
            val wsToken = res.body!!.string()
            Log.d("wstoken", wsToken)
            val socketURL = "${websocketURL}/groups/connect/${wsToken}"
            val request = Request.Builder()
                .url(socketURL)
                .build()
            AppInfo.httpClient.newWebSocket(request, this)
            this.group2 = group
        }

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        this.webSocket = webSocket
        delegate?.let {
            delegate!!.onGroupConnect(group2)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        this.webSocket = webSocket
        try {
            Log.d("sync-onmessage", bytes.utf8())
            val message = json.decodeFromString<WSMessage>(bytes.utf8())
            val type = WSMessageType.fromInt(message.type)
            when (type) {
                WSMessageType.GROUP_UPDATE -> {
                    val group = json.decodeFromString<SQGroup>(message.data)
                    if (PlayPauseState.fromInt(group.playbackState?.state ?: 1) == PlayPauseState.PAUSE) {
                        delegate?.let {
                            delegate!!.onPause(message)
                        }
                    }
                    delegate?.let {
                        delegate!!.onGroupUpdate(group, message)
                    }
                }
                WSMessageType.NEXT_SONG -> {
                    delegate?.let {
                        delegate!!.onNextSong(message)
                    }
                }
                WSMessageType.GO_BACK -> {
                    delegate?.let {
                        delegate!!.onPrevSong(message)
                    }
                }
                WSMessageType.PLAY -> {
                    delegate?.let {
                        delegate!!.onPlay(message)
                    }
                }
                WSMessageType.PAUSE -> {
                    delegate?.let {
                        delegate!!.onPause(message)
                    }
                }
                WSMessageType.TIMESTAMP_UPDATE -> {
                    val tsUpdateInfo = json.decodeFromString<WSTimestampUpdate>(message.data)
                    delegate?.let {
                        delegate!!.onTimestampUpdate(tsUpdateInfo.timestamp, message)
                    }
                }
                WSMessageType.UNKNOWN -> {

                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("sync-onmessage", "error with something ${e}")
        }
    }

    fun pauseSong() {
        if (::webSocket.isInitialized) {
            val data = WSMessage(
                type = WSMessageType.PAUSE.ordinal,
                data = "",
                sentAt = Date().toInstant().toEpochMilli().toDouble()
            )
            val jsonData = json.encodeToString(data)
            webSocket.send(jsonData)
        }
    }
     fun playSong() {
        if (::webSocket.isInitialized) {
            val data = WSMessage(
                type = WSMessageType.PLAY.ordinal,
                data = "",
                sentAt = Date().toInstant().toEpochMilli().toDouble()
            )
            val jsonData = json.encodeToString(data)
            webSocket.send(jsonData)
        }
    }
     fun nextSong() {
        if (::webSocket.isInitialized) {
            val data = WSMessage(
                type = WSMessageType.NEXT_SONG.ordinal,
                data = "",
                sentAt = Date().toInstant().toEpochMilli().toDouble()
            )
            val jsonData = json.encodeToString(data)
            webSocket.send(jsonData)
        }
    }
     fun addToQueue(song: SQSong, user: SQUser) {
        if (::webSocket.isInitialized) {
            val songData = SQQueueItem(
                song = song,
                addedBy = user.username
            )
            val data = WSMessage(
                type = WSMessageType.PLAY.ordinal,
                data = json.encodeToString(songData),
                sentAt = Date().toInstant().toEpochMilli().toDouble()
            )
            val jsonData = json.encodeToString(data)
            webSocket.send(jsonData)
        }
    }
     fun disconnect() {
        if(::webSocket.isInitialized) {
            webSocket.close(1000, "user disconnected")
        }
    }
     fun playbackStarted() {
        if (::webSocket.isInitialized) {
            val data = WSMessage(
                type = WSMessageType.PLAYBACK_STARTED.ordinal,
                data = "",
                sentAt = Date().toInstant().toEpochMilli().toDouble()
            )
            val jsonData = json.encodeToString(data)
            webSocket.send(jsonData)
        }
    }
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("onclosed", "${code} ${reason}")
        delegate?.let {
            delegate!!.onDisconnect()
        }
    }

}
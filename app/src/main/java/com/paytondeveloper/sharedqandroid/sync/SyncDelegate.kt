package com.paytondeveloper.sharedqandroid.sync

import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.protocol.WSMessage

interface SyncDelegate {
    fun onGroupConnect(group: SQGroup)
    fun onGroupUpdate(group: SQGroup, message: WSMessage)
    fun onNextSong(message: WSMessage)
    fun onPrevSong(message: WSMessage)
    fun onPlay(message: WSMessage)
    fun onPause(message: WSMessage)
    fun onTimestampUpdate(timestamp: Double, message: WSMessage)
    fun onSeekTo(timestamp: Double, message: WSMessage)
    fun onDisconnect()
}
package com.paytondeveloper.sharedqandroid.musicservices

import com.paytondeveloper.sharedqandroid.protocol.SQSong

interface MusicService {
    suspend fun getMostRecentSong(): SQSong
    suspend fun recentlyPlayed(): List<SQSong>
    suspend fun playSong(song: SQSong)
    suspend fun playAt(timestamp: Double)
    suspend fun getSongTimestamp(): Double
    suspend fun stopPlayback()
    suspend fun nextSong()
    suspend fun addQueue(queue: List<SQSong>)
    suspend fun pauseSong()
    suspend fun prevSong()
    suspend fun seekTo(timestamp: Double)
    suspend fun searchFor(query: String): List<SQSong>?
    suspend fun registerStateListeners()
}
//
//  File.swift
//  
//
//  Created by Payton Curry on 9/17/24.
//

import Foundation
import SharedQProtocol


#if SKIP
import com.apple.android.sdk.authentication.AuthenticationFactory
import com.apple.android.sdk.authentication.AuthenticationManager
import com.apple.android.sdk.authentication.TokenResult
import android.content.Context
import android.content.Intent
class AMManager: MusicService {
    let applicationContext = ProcessInfo.processInfo.androidContext
    private var authenticationManager: AuthenticationManager = AuthenticationFactory.createAuthenticationManager(
            applicationContext)
    static var shared = AMManager()
    private var devToken = "eyJhbGciOiJFUzI1NiIsImtpZCI6IldRMjRWOVozNjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJCUjkyNk5ZOUZTIiwiZXhwIjoxNzQwMzczMzQ5LCJpYXQiOjE3MjUyNDk3NDl9.eui6rCeSsHYagtnHhvClTWsbmOMhCzwlO-Fx3kgGfj5l4XjfSQQwrGXhBw-EoRYmF6Sv5Ehqu6tmAvlgNkOriw"
    func openAuthLink() -> android.content.Intent {
            var intent = authenticationManager.createIntentBuilder(devToken)
                .setStartScreenMessage("Connect SharedQ to Apple Music")
                .setHideStartScreen(true)
                .build()
            return intent
        }
    func getMostRecentSong() async -> SharedQProtocol.SQSong? {
        return SQSong.testSongs.randomElement()
    }
    
    func recentlyPlayed() async -> [SharedQProtocol.SQSong] {
        return SQSong.testSongs
    }
    // SKIP DECLARE: override suspend fun playSong(song: shared.qprotocol.SQSong): Unit
    func playSong(song: SharedQProtocol.SQSong) async {
        logger.log(level: .debug, "Playing song: \(song.title)")
    }
    
    func playAt(timestamp: TimeInterval) async {
        logger.log(level: .debug, "Playing at: \(timestamp)")
    }
    
    func getSongTimestamp() async -> TimeInterval {
        return TimeInterval.random(in: 0.0...120.0)
    }
    
    func stopPlayback() async {
        logger.log(level: .debug, "Stopping playback")
    }
    
    func nextSong() async {
        logger.log(level: .debug, "Skipping to next song")
    }
    
    // SKIP DECLARE: override suspend fun addQueue(queue: Array<shared.qprotocol.SQSong>): Unit
    func addQueue(queue: [SharedQProtocol.SQSong]) async {
        
        for song in queue {
            logger.log(level: .debug, "Adding song \(song.title) to queue")
        }
    }
    
    func pauseSong() async {
        logger.log(level: .debug, "Pausing playback")
    }
    
    func prevSong() async {
        logger.log(level: .debug, "Skipping to prev song")
    }
    
    func seekTo(timestamp: TimeInterval) async {
        logger.log(level: .debug, "Seeking to \(timestamp)")
    }
    
    func searchFor(_ query: String) async -> [SharedQProtocol.SQSong] {
        logger.log(level: .debug, "Search request for: \(query)")
        
        return SQSong.testSongs
    }
    
    func registerStateListeners() async {
        logger.log(level: .debug, "Registering state listeners...")
    }
    
}
#endif

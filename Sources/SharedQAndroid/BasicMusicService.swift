//
//  BasicMusicService.swift
//
//
//  Created by Payton Curry on 6/2/24.
//
// A basic MusicService implementation for testing until I get actual streaming services implemented

import Foundation
import SharedQProtocol

extension SQSong {
    static var testSongs = [SQSong(title: "Test Song", artist: "Basic Artist", albumArt: URL(string: "https://glassnotemusic.com/wp-content/uploads/2018/03/CHVRCHES_ALBUM_COVER_1.10.18.jpg"), colors: ["#a83244", "#a8329b"], textColor: "#FFFFFF", duration: 120), SQSong(title: "California", artist: "CHVRCHES", albumArt: URL(string: "https://i.discogs.com/SXn0SqimPLQGSJbZK88oEwg4PPB5AOi6XpPfswY5ZMo/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTIwNzkz/MTk5LTE2MzU2NTIw/MTctMTg1Ny5qcGVn.jpeg"), colors: ["#a83244", "#a8329b"], textColor: "#FFFFFF", duration: 120)]
}

class BasicMusicService: MusicService {
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

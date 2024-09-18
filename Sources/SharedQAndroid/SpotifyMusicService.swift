//
//  File.swift
//  
//
//  Created by Payton Curry on 6/3/24.
//

import Foundation
import SharedQProtocol

#if SKIP
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
#endif

class SpotifyMusicService: MusicService {
    init() {
        
    }
    func getMostRecentSong() async -> SharedQProtocol.SQSong? {
        nil
    }
    
    func recentlyPlayed() async -> [SharedQProtocol.SQSong] {
        []
    }
    
    // SKIP DECLARE: override suspend fun playSong(song: shared.qprotocol.SQSong): Unit
    func playSong(song: SharedQProtocol.SQSong) async {
        
    }
    
    func playAt(timestamp: TimeInterval) async {
        
    }
    
    func getSongTimestamp() async -> TimeInterval {
        0.0
    }
    
    func stopPlayback() async {
        
    }
    
    func nextSong() async {
        
    }
    
    // SKIP DECLARE: override suspend fun addQueue(queue: Array<shared.qprotocol.SQSong>): Unit
    func addQueue(queue: [SharedQProtocol.SQSong]) async {
        
    }
    
    func pauseSong() async {
        
    }
    
    func prevSong() async {
        
    }
    
    func seekTo(timestamp: TimeInterval) async {
        
    }
    
    func searchFor(_ query: String) async -> [SharedQProtocol.SQSong] {
        return []
    }
    
    func registerStateListeners() async {
        
    }
    
    
}

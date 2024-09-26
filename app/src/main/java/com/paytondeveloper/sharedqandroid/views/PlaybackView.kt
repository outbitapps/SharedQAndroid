package com.paytondeveloper.sharedqandroid.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apple.android.music.playback.model.PlaybackState
import com.paytondeveloper.sharedqandroid.protocol.PlayPauseState
import com.paytondeveloper.sharedqandroid.protocol.SQPlaybackState
import com.paytondeveloper.sharedqandroid.sync.SQManager
import com.paytondeveloper.sharedqandroid.views.components.SongImage

@Composable
fun PlaybackView() {
    val state by SQManager.shared.uiState.collectAsState()
    if (state.connectedGroup != null) {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(it), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                SongImage(state.connectedGroup!!.currentlyPlaying, size = 300.dp)
                Row {
                    if (state.connectedGroup!!.playbackState?.state == PlayPauseState.PAUSE.ordinal) {
                        Button(onClick = {
                            SQManager.shared.syncManager.playSong()

                        }) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                        }
                    } else {
                        Button(onClick = {
                            SQManager.shared.syncManager.pauseSong()
                        }) {
                            Text("||")
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }
}
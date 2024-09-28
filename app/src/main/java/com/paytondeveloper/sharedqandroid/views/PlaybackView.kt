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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.apple.android.music.playback.model.PlaybackState
import com.paytondeveloper.sharedqandroid.R
import com.paytondeveloper.sharedqandroid.protocol.PlayPauseState
import com.paytondeveloper.sharedqandroid.protocol.SQPlaybackState
import com.paytondeveloper.sharedqandroid.sync.SQManager
import com.paytondeveloper.sharedqandroid.views.Group.Components.BaseQueueView
import com.paytondeveloper.sharedqandroid.views.components.SongImage

@Composable
fun PlaybackView(nav: NavController) {
    val state by SQManager.shared.uiState.collectAsState()
    if (state.connectedGroup != null) {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(it), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                SongImage(state.connectedGroup!!.currentlyPlaying, size = 300.dp)
                Row {
                    Button(onClick = {
                    }) {
                        Icon(painter = painterResource(R.drawable.skip_previous_24px), contentDescription = "Last song")
                    }
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
                    Button(onClick = {
                        SQManager.shared.syncManager.nextSong()
                    }) {
                        Icon(painter = painterResource(R.drawable.skip_next_24px), contentDescription = "Next song")
                    }
                }
                Button(
                    onClick = {
                        nav.navigate("playback/queue")
                    }
                ) {
                    Text("View Queue")
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PlaybackQueueView(nav: NavController) {
    val state by SQManager.shared.uiState.collectAsState()
    Scaffold {
        Column(modifier = Modifier.fillMaxSize().padding(it).padding(12.dp)) {
            Row {
                if (state.connectedGroup!!.currentlyPlaying != null) {
                    val currentlyPlaying = state.connectedGroup!!.currentlyPlaying!!
                    SongImage(currentlyPlaying)
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(currentlyPlaying.title)
                        Text(currentlyPlaying.artist)
                    }
                }
            }
            BaseQueueView(state.connectedGroup!!.previewQueue)
            Button(
                onClick = {
                    nav.navigate("playback/queue/add")
                }
            ) {
                Text("Add to Queue")
            }
        }
    }
}
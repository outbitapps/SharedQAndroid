package com.paytondeveloper.sharedqandroid.views.Group

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paytondeveloper.sharedqandroid.AppInfo
import com.paytondeveloper.sharedqandroid.Main
import com.paytondeveloper.sharedqandroid.musicservices.AppleMusicService
import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.sync.SQManager
import com.paytondeveloper.sharedqandroid.views.components.SongImage


@Composable
fun JoinGroupView(nav: NavController, group: SQGroup) {
    Scaffold {
        Column(modifier = Modifier.padding(it).fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(group.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
            Text("is currently listening to...", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            SongImage(group.currentlyPlaying, size = 300.dp)
            Text(group.currentlyPlaying?.title ?: "Nothing", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 12.dp))
            Text(group.currentlyPlaying?.artist ?: "Nobody", modifier = Modifier.alpha(0.8f))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Are you sure you want to join?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("everybody currently in “THE BOYS\uD83E\uDD76” will be able to see that you joined, and will be able to see your profile. if you would like to know who is currently listening in this group, press “View Queue” (nobody will know that you’re viewing the queue). once you join, the music in the queue will start playing immediately.", Modifier.alpha(0.8f))
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Button(modifier = Modifier.fillMaxWidth(0.5f).padding(2.dp).height(70.dp), shape = RoundedCornerShape(12.dp),onClick = {
                        var prefs = AppInfo.application.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        SQManager.shared.syncManager.connectToGroup(group, prefs.getString("token", "nothing")!!)
                    }) {
                        Text("Join")
                    }
                    Button(modifier = Modifier.fillMaxWidth().padding(2.dp).height(70.dp), shape = RoundedCornerShape(12.dp),onClick = {
                        nav.navigate("previewqueue/${group.id}")
                    }) {
                        Text("View Queue")
                    }
                }
                Button(modifier = Modifier.fillMaxWidth().padding(2.dp).height(70.dp), onClick = {
                    nav.popBackStack()
                }, shape = RoundedCornerShape(12.dp)) {
                    Text("Cancel")
                }
            }
        }
    }
}
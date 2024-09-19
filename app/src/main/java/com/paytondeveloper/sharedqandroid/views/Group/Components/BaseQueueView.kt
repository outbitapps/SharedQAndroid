package com.paytondeveloper.sharedqandroid.views.Group.Components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import com.paytondeveloper.sharedqandroid.protocol.SQQueueItem
import com.paytondeveloper.sharedqandroid.views.components.SongImage

@Composable
fun BaseQueueView(queue: List<SQQueueItem>) {
    Scaffold {
        Column(modifier = Modifier.padding(it).fillMaxHeight().scrollable(rememberScrollState(), Orientation.Vertical)) {
            queue.forEach {
                QueueItemView(item = it)
            }
        }
    }
}
@Composable
fun QueueItemView(item: SQQueueItem) {
    ListItem(
        headlineContent = {
            Text(item.song.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(item.song.artist, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.alpha(0.8f))
        },
        leadingContent = {
            SongImage(item.song)
        },
        trailingContent = {
            Text("Added by ${item.addedBy}")
        }
    )
}
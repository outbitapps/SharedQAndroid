package com.paytondeveloper.sharedqandroid.views.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paytondeveloper.sharedqandroid.R
import com.paytondeveloper.sharedqandroid.protocol.SQSong

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun SongImage(song: SQSong?, size: Dp = 50.dp) {
    var isLoaded by remember { mutableStateOf(false) }
    if (song == null) {
        Image(modifier = Modifier.clip(RoundedCornerShape(7.dp)).size(size),painter = painterResource(
            R.drawable.mediaitemplaceholder), contentDescription =  "Placeholder song icon")
    } else {
        Crossfade(targetState = isLoaded, label = "pltoimg") { it ->
            Log.d("songimg", song.albumArt.toString())
            Box {
                AsyncImage(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(7.dp))
                        .alpha(if (it) 1.0f else 0.5f),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(song.albumArt!!.toString())
                        .crossfade(true)
                        .build()
                    ,
                    contentDescription = "${song.title} cover art",
                    placeholder = painterResource(id = R.drawable.mediaitemplaceholder),
                    error = painterResource(id = R.drawable.mediaitemplaceholder),
                    onSuccess = {
                        isLoaded = true
                    }
                )
                if (!it) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
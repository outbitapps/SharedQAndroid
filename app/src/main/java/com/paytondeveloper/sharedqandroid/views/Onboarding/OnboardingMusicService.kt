package com.paytondeveloper.sharedqandroid.views.Onboarding

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paytondeveloper.sharedqandroid.AppInfo
import com.paytondeveloper.sharedqandroid.OnboardingPage
import com.paytondeveloper.sharedqandroid.R
import com.paytondeveloper.sharedqandroid.appleResultLauncher
import com.paytondeveloper.sharedqandroid.musicservices.AppleMusicService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun OnboardingMusicService(nav: NavController) {
    val uriHandler = LocalUriHandler.current
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Choose a service", style = MaterialTheme.typography.titleLarge)
//            AuthCard("Spotify", {
//                GlobalScope.launch { uriHandler.openUri(SPTManager.shared.openAuthURL()) };
//            }, {
//
//            }, AppInfo.musicServices.contains("spotify"))
            AuthCard("Apple Music") {
                val intent = AppleMusicService.shared.openAuthLink()

                appleResultLauncher.launch(intent)
                AppInfo.setOnboardingPage(OnboardingPage.Complete)
                nav.navigate("home")
            }
        }
    }
}

@Composable
fun AuthCard(name: String, authRequest: () -> Any) {
    var openAlertDialog by remember { mutableStateOf(false) }
    Surface(onClick = {
            authRequest()
    }) {
        Box(modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(23.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(23.dp))
            .fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier.clip(RoundedCornerShape(11.dp)).size(50.dp),painter = painterResource(
                    R.drawable.mediaitemplaceholder), contentDescription =  "${name} icon")
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(name)
                }
            }
        }
    }
}
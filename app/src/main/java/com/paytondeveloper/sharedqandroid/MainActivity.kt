package com.paytondeveloper.sharedqandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paytondeveloper.sharedqandroid.musicservices.AppleMusicService
import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.sync.SQManager
import com.paytondeveloper.sharedqandroid.ui.theme.SharedQTheme
import com.paytondeveloper.sharedqandroid.views.CreateGroupView
import com.paytondeveloper.sharedqandroid.views.Group.Components.BaseQueueView
import com.paytondeveloper.sharedqandroid.views.Group.JoinGroupView
import com.paytondeveloper.sharedqandroid.views.Onboarding.OnboardingMusicService
import com.paytondeveloper.sharedqandroid.views.Onboarding.OnboardingWelcomeView
import com.paytondeveloper.sharedqandroid.views.PlaybackView
import com.paytondeveloper.sharedqandroid.views.components.SongImage


lateinit var appleResultLauncher: ActivityResultLauncher<Intent>

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        appleResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("result", result.toString())
            result.data?.let {
                var tokenResult = AppleMusicService.shared.handleAuthIntent(it)
                Log.d("token", tokenResult.toString())
                if (!tokenResult.isError) {
                    AppleMusicService.shared.storeToken(tokenResult.musicUserToken)
                } else {
                    Log.e("token", tokenResult.error.toString())
                }
            }
        }
        System.loadLibrary("c++_shared")
        System.loadLibrary("appleMusicSDK")
        AppInfo.application = application
        AppleMusicService.shared
        SQManager.shared
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedQTheme {
                    Main()
            }
        }
    }
}

@Composable
fun Main() {
    val viewModel by SQManager.shared.uiState.collectAsState()

//    Scaffold() {
//        Column(modifier = Modifier.padding(it)) {
//            OnboardingWelcomeView()
//        }
//    }
    val navController = rememberNavController()
    NavHost(navController = navController,startDestination = AppInfo.onboardingPage.route) {
        composable("home") {
            HomeView(nav = navController)
        }
        composable("auth") {
            OnboardingWelcomeView(nav = navController)
        }
        composable("musicservice") {
            OnboardingMusicService(nav = navController)
        }
        composable("tutorial") {

        }
        composable("joingroup/{groupID}", arguments = listOf(navArgument("groupID") { type = NavType.StringType})) { proxy ->
            var groupID = proxy.arguments?.getString("groupID")
            var group = viewModel.currentUser!!.groups.first { it.id == groupID }
            JoinGroupView(nav = navController, group = group)
        }
        composable("previewqueue/{groupID}", arguments = listOf(navArgument("groupID") { type = NavType.StringType})) { proxy ->
            var groupID = proxy.arguments?.getString("groupID")
            var group = viewModel.currentUser!!.groups.first { it.id == groupID }
            BaseQueueView(group.previewQueue)
        }
    }
    if (viewModel.connectedGroup != null) {
        PlaybackView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(nav: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val viewModel by SQManager.shared.uiState.collectAsState()
    Scaffold(topBar = { TopAppBar(
        title = { Text("SharedQ") }
    ) }, floatingActionButton = {
        FloatingActionButton(onClick = {
            showBottomSheet = true
        }) {
            Icon(Icons.Rounded.Add, "Create Group")
        }
    }) {
        Column(modifier = Modifier.padding(it).scrollable(rememberScrollState(), orientation = Orientation.Vertical)) {
            viewModel.currentUser?.groups?.forEach {
                Surface(onClick = {
                    nav.navigate("joingroup/${it.id}")
                }) {
                    HomeGroupCell(it)
                }
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(modifier = Modifier.fillMaxHeight(), onDismissRequest = {
            showBottomSheet = false
        }) {
            CreateGroupView()
        }
    }
}

@Composable
fun HomeGroupCell(group: SQGroup) {
    ListItem(
        headlineContent = {
            Text(group.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text("${group.members.count()} members • ${group.connectedMembers.count()} now")
        },
        trailingContent = {
            Row {
                group.currentlyPlaying?.let { song ->
                    SongImage(song)
                }
                Icon(Icons.Rounded.KeyboardArrowRight, "Right arrow")
            }
        }
    )
}
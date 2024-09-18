package com.paytondeveloper.sharedqandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paytondeveloper.sharedqandroid.ui.theme.SharedQTheme
import com.paytondeveloper.sharedqandroid.views.OnboardingWelcomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
//    Scaffold() {
//        Column(modifier = Modifier.padding(it)) {
//            OnboardingWelcomeView()
//        }
//    }
    OnboardingWelcomeView()
}


package com.paytondeveloper.sharedqandroid.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingWelcomeView() {
    Scaffold {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .padding(12.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.padding(start = 6.dp)) {
                Text(text = "Welcome to SharedQ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(text = "create a shared music queue no matter what streaming service you use", modifier = Modifier
                    .padding(top = 6.dp)
                    .padding(end = 12.dp))
            }
            EmailAuthComponent()
        }
    }
}

@Composable
fun EmailAuthComponent(modifier: Modifier = Modifier) {
    var viewState by remember { mutableStateOf<EmailAuthView>(EmailAuthView.none) }
    Box(modifier = modifier
        .clip(
            RoundedCornerShape(16.dp)
        )
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
        .animateContentSize()
        .height(if (viewState != EmailAuthView.none) 400.dp else 60.dp)) {
        AnimatedContent(viewState) { state ->
            when (state) {
                EmailAuthView.none -> {
                    Row(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Icon(Icons.Rounded.Email, "Email", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(start = 12.dp))
                        Spacer(Modifier.width(12.dp))
                        Row {
                            Button(modifier = Modifier.padding(end = 12.dp), shape = RoundedCornerShape(8.dp), onClick = {
                                viewState = EmailAuthView.signUp
                            }) {
                                Text("Sign Up")
                            }
                            Button(shape = RoundedCornerShape(8.dp), onClick = {
                                viewState = EmailAuthView.signIn
                            }) {
                                Text("Sign In")
                            }
                        }
                    }
                }
                EmailAuthView.signIn -> {
                    Column(modifier = Modifier.fillMaxWidth()) {

                    }
                }
                EmailAuthView.signUp -> {
                    var email by remember { mutableStateOf("") }
                    var username by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                value = username,
                                onValueChange = { input ->
                                    username = input
                                },
                                label = { Text("Username") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                value = email,
                                onValueChange = { input ->
                                    email = input
                                },
                                label = { Text("Email") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                value = password,
                                onValueChange = { input ->
                                    password = input
                                },
                                label = { Text("Password") },
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                        Button(modifier = Modifier.padding(8.dp).fillMaxWidth(), shape = RoundedCornerShape(8.dp), onClick = {
                            viewState = EmailAuthView.none
                        }) {
                            Text("Sign Up")
                        }
                    }
                }
            }
        }
    }
}



enum class EmailAuthView {
    none,
    signUp,
    signIn
}
package com.paytondeveloper.sharedqandroid.views

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paytondeveloper.sharedqandroid.protocol.SQDefaultPermissions
import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.protocol.SQGroupMember
import com.paytondeveloper.sharedqandroid.sync.SQManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CreateGroupView() {
    var groupName by remember { mutableStateOf("") }
    var publicGroup by remember { mutableStateOf(false) }
    var membersControlPlayback by remember { mutableStateOf(true) }
    var membersAddToQueue by remember { mutableStateOf(true) }
    var askToJoin by remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(12.dp),verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
        Column {
            Text(text = "Create Group", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(text = "a group is a shared queue. you can make any type of group you want! anywhere from a massive, public group with hundreds of people at a time to a smaller, private group with a couple friends or some family. you can customize it however you’d like!")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = groupName,
                onValueChange = {
                    groupName = it
                },
                label = {
                    Text("Group name")
                }
            )
            ListItem(
                headlineContent = {
                    Text("Public Group", fontWeight = FontWeight.SemiBold)
                },
                supportingContent = {
                    Text("In a public group, the group is visible to anyone and can be joined by everyone. Best for large communities")
                },
                trailingContent = {
                    Switch(
                        checked = publicGroup,
                        onCheckedChange = {
                            publicGroup = it
                        }
                    )
                }
            )
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(top = 12.dp)) {
                Text("Default Permissions", fontWeight = FontWeight.SemiBold)
                ListItem(
                    headlineContent = {
                        Text("Members can control playback")
                    },
                    trailingContent = {
                        Switch(
                            checked = membersControlPlayback,
                            onCheckedChange = {
                                membersControlPlayback = it
                            }
                        )
                    }
                )
                ListItem(
                    headlineContent = {
                        Text("Members can add to queue")
                    },
                    trailingContent = {
                        Switch(
                            checked = membersAddToQueue,
                            onCheckedChange = {
                                membersAddToQueue = it
                            }
                        )
                    }
                )
                ListItem(
                    headlineContent = {
                        Text("Ask to join")
                    },
                    supportingContent = {
                        Text("With Ask to join, your permission is needed before anyone can join. Best for small groups (NOT FUNCTIONAL IN BETA)")
                    },
                    trailingContent = {
                        Switch(
                            checked = askToJoin,
                            onCheckedChange = {
                                askToJoin = it
                            }
                        )
                    }
                )
            }
        }
        Button(modifier = Modifier.padding(8.dp).fillMaxWidth(), onClick = {
            var group = SQGroup(
                id = UUID.randomUUID().toString(),
                name = groupName,
                defaultPermissions = SQDefaultPermissions(
                    membersCanAddToQueue = membersAddToQueue,
                    membersCanControlPlayback = membersControlPlayback
                ),
                publicGroup = publicGroup,
                askToJoin = askToJoin,
                members = listOf(SQGroupMember(
                    id = UUID.randomUUID().toString(),
                    user = SQManager.shared.uiState.value.currentUser!!,
                    isOwner = true,
                    defaultPermissions = SQDefaultPermissions(
                        membersCanAddToQueue = true,
                        membersCanControlPlayback = true
                    )
                ))
            )
            GlobalScope.launch {
                SQManager.shared.createGroup(group)
            }
        }) {
            Text("Create Group")
        }
    }
}
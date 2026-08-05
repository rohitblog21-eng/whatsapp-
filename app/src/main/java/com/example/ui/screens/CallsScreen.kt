package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.E2ESecurityBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CipherGreen
import com.example.ui.theme.EmeraldPrimary

data class CallLog(
    val id: String,
    val contactName: String,
    val contactAvatar: String,
    val isVideo: Boolean,
    val isIncoming: Boolean,
    val timestamp: String,
    val duration: String
)

@Composable
fun CallsScreen(
    onStartCall: (String, String, Boolean) -> Unit
) {
    val sampleLogs = listOf(
        CallLog("c1", "Alex Rivera (Lead SecOps)", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", isVideo = false, isIncoming = true, timestamp = "Today, 10:45 AM", duration = "14:20"),
        CallLog("c2", "Sophia Chen (AI Lead)", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150", isVideo = true, isIncoming = false, timestamp = "Yesterday, 4:15 PM", duration = "08:12"),
        CallLog("c3", "Marcus Vance (Cryptography)", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", isVideo = false, isIncoming = true, timestamp = "Aug 3, 2:30 PM", duration = "02:45")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        E2ESecurityBadge()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Encrypted Calls",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row {
                IconButton(
                    onClick = { onStartCall("Alex Rivera", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", false) },
                    modifier = Modifier.testTag("quick_voice_call")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = EmeraldPrimary)
                }
                IconButton(
                    onClick = { onStartCall("Sophia Chen", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150", true) },
                    modifier = Modifier.testTag("quick_video_call")
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = EmeraldPrimary)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(sampleLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onStartCall(log.contactName, log.contactAvatar, log.isVideo) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            name = log.contactName,
                            avatarUrl = log.contactAvatar,
                            sizeDp = 48
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = log.contactName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CipherGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (log.isIncoming) Icons.Default.CallReceived else Icons.Default.CallMade,
                                    contentDescription = null,
                                    tint = if (log.isIncoming) CipherGreen else EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${log.timestamp} • ${log.duration}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { onStartCall(log.contactName, log.contactAvatar, log.isVideo) }) {
                            Icon(
                                imageVector = if (log.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = "Call Back",
                                tint = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CallState
import com.example.ui.theme.CipherGreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay

@Composable
fun CallingOverlay(
    callState: CallState,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleScreenShare: () -> Unit,
    onToggleNoiseSuppression: () -> Unit
) {
    if (callState.activeCallId == null) return

    var secondsElapsed by remember { mutableStateOf(0) }

    LaunchedEffect(callState.activeCallId) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val minutes = secondsElapsed / 60
    val secs = secondsElapsed % 60
    val timeFormatted = String.format("%02d:%02d", minutes, secs)

    Dialog(
        onDismissRequest = { /* keep open until explicitly ended */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("active_call_screen"),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top E2EE Badge & Status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "E2EE Call",
                                tint = CipherGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "E2E Encrypted Call • ${callState.e2eFingerprint.take(18)}...",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (callState.isVideo) "Encrypted Video Call" else "Encrypted Voice Call",
                        fontSize = 14.sp,
                        color = CipherGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Center Avatar & Name
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UserAvatar(
                        name = callState.contactName,
                        avatarUrl = callState.contactAvatar,
                        sizeDp = 120
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = callState.contactName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = CipherGreen
                    )

                    if (callState.isNoiseSuppressed) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Noise suppression",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Noise Cancellation Active",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Bottom Call Controls
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute toggle
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isMuted) DangerRed else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }

                        // Speaker toggle
                        IconButton(
                            onClick = onToggleSpeaker,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isSpeakerOn) EmeraldPrimary else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }

                        // Screen share toggle
                        IconButton(
                            onClick = onToggleScreenShare,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isScreenSharing) EmeraldPrimary else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = if (callState.isScreenSharing) Icons.Default.ScreenShare else Icons.Default.StopScreenShare,
                                contentDescription = "Screen Share",
                                tint = Color.White
                            )
                        }

                        // Noise cancellation toggle
                        IconButton(
                            onClick = onToggleNoiseSuppression,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isNoiseSuppressed) EmeraldPrimary else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Noise Suppress",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // End call red button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .testTag("end_call_button")
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(DangerRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

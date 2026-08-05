package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.ui.components.ChatBubbleItem
import com.example.ui.components.E2ESecurityBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CipherGreen
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chat: Chat?,
    messages: List<Message>,
    isDarkTheme: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String, MessageType, String?, String?, String?, String?) -> Unit,
    onStartCall: (String, String, Boolean) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onToggleStar: (String, Boolean) -> Unit,
    onTogglePin: (String, Boolean) -> Unit
) {
    if (chat == null) return

    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForMenu by remember { mutableStateOf<Message?>(null) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* inspect chat info */ }
                    ) {
                        UserAvatar(
                            name = chat.name,
                            avatarUrl = chat.avatarUrl,
                            isOnline = chat.isOnline,
                            sizeDp = 40
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = chat.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "E2EE",
                                    tint = CipherGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = if (chat.isOnline) "Online • Encrypted" else chat.lastSeen,
                                fontSize = 11.sp,
                                color = if (chat.isOnline) CipherGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onStartCall(chat.name, chat.avatarUrl, false) },
                        modifier = Modifier.testTag("start_voice_call")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call")
                    }
                    IconButton(
                        onClick = { onStartCall(chat.name, chat.avatarUrl, true) },
                        modifier = Modifier.testTag("start_video_call")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // E2E Notice
            E2ESecurityBadge()

            // Messages Scroll View
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        isDarkTheme = isDarkTheme,
                        onLongClick = { selectedMessageForMenu = msg }
                    )
                }
            }

            // Message Context Menu Dialog / Popup
            if (selectedMessageForMenu != null) {
                val msg = selectedMessageForMenu!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Message Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(onClick = { selectedMessageForMenu = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = {
                                replyingToMessage = msg
                                selectedMessageForMenu = null
                            }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Reply, contentDescription = "Reply")
                                    Text("Reply", fontSize = 10.sp)
                                }
                            }
                            IconButton(onClick = {
                                onToggleStar(msg.id, msg.isStarred)
                                selectedMessageForMenu = null
                            }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Star, contentDescription = "Star", tint = Color(0xFFF59E0B))
                                    Text(if (msg.isStarred) "Unstar" else "Star", fontSize = 10.sp)
                                }
                            }
                            IconButton(onClick = {
                                onTogglePin(msg.id, msg.isPinned)
                                selectedMessageForMenu = null
                            }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.PushPin, contentDescription = "Pin")
                                    Text(if (msg.isPinned) "Unpin" else "Pin", fontSize = 10.sp)
                                }
                            }
                            IconButton(onClick = {
                                onDeleteMessage(msg.id)
                                selectedMessageForMenu = null
                            }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    Text("Delete", fontSize = 10.sp, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            // Reply Preview Bar
            AnimatedVisibility(visible = replyingToMessage != null) {
                if (replyingToMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyingToMessage?.senderName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = replyingToMessage?.text ?: "",
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { replyingToMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel reply")
                        }
                    }
                }
            }

            // Attachment Options Drawer
            AnimatedVisibility(visible = showAttachmentMenu) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Image
                    IconButton(onClick = {
                        onSendMessage(
                            "Shared a photo",
                            MessageType.IMAGE,
                            "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=500",
                            "photo.jpg",
                            replyingToMessage?.id,
                            replyingToMessage?.text
                        )
                        showAttachmentMenu = false
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = "Image", tint = EmeraldPrimary)
                            Text("Image", fontSize = 10.sp)
                        }
                    }

                    // PDF Document
                    IconButton(onClick = {
                        onSendMessage(
                            "Security_Audit_Report.pdf",
                            MessageType.PDF,
                            "https://example.com/sec_report.pdf",
                            "Security_Audit_Report.pdf",
                            replyingToMessage?.id,
                            replyingToMessage?.text
                        )
                        showAttachmentMenu = false
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.Red)
                            Text("PDF", fontSize = 10.sp)
                        }
                    }

                    // Code Snippet
                    IconButton(onClick = {
                        onSendMessage(
                            "val key = Argon2.generateKey()\nval cipher = AES256GCM.encrypt(data, key)",
                            MessageType.CODE,
                            null,
                            null,
                            replyingToMessage?.id,
                            replyingToMessage?.text
                        )
                        showAttachmentMenu = false
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Code, contentDescription = "Code", tint = CipherGreen)
                            Text("Code", fontSize = 10.sp)
                        }
                    }

                    // Quick AI Prompt Trigger
                    IconButton(onClick = {
                        inputText = "@AI "
                        showAttachmentMenu = false
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFF8B5CF6))
                            Text("Ask AI", fontSize = 10.sp)
                        }
                    }
                }
            }

            // Bottom Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Encrypted message or @AI prompt...", fontSize = 13.sp) },
                    singleLine = false,
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field")
                )

                Spacer(modifier = Modifier.width(6.dp))

                if (inputText.isBlank()) {
                    // Voice note send button
                    IconButton(
                        onClick = {
                            onSendMessage(
                                "Voice Note",
                                MessageType.AUDIO,
                                null,
                                "voice_note.mp3",
                                replyingToMessage?.id,
                                replyingToMessage?.text
                            )
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .testTag("voice_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Record Voice Note",
                            tint = Color.White
                        )
                    }
                } else {
                    // Send text message button
                    IconButton(
                        onClick = {
                            onSendMessage(
                                inputText,
                                MessageType.TEXT,
                                null,
                                null,
                                replyingToMessage?.id,
                                replyingToMessage?.text
                            )
                            inputText = ""
                            replyingToMessage = null
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CallingOverlay
import com.example.ui.theme.CipherGreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.WhisperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WhisperViewModel
) {
    val authState by viewModel.authUiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val chatFilterTab by viewModel.chatFilterTab.collectAsState()
    val activeChatId by viewModel.activeChatId.collectAsState()
    val filteredChats by viewModel.filteredChats.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val securityLogs by viewModel.securityLogs.collectAsState()
    val deviceSessions by viewModel.deviceSessions.collectAsState()
    val callState by viewModel.callState.collectAsState()

    var activeChatScreenView by remember { mutableStateOf(false) } // true if inspecting specific chat
    var securityTabSubView by remember { mutableStateOf(0) } // 0: Security, 1: Admin Panel

    if (!authState.isLoggedIn) {
        AuthScreen(
            authUiState = authState,
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onSignup = { email, uname, pass -> viewModel.signup(email, uname, pass) },
            onVerifyOtp = { otp -> viewModel.verifyOtp(otp) },
            onVerify2FA = { code -> viewModel.verify2FA(code) }
        )
    } else {
        Scaffold(
            topBar = {
                if (activeTab != 0 || !activeChatScreenView) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Whisper AI Secure Chat",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "E2E Encrypted • OWASP Top 10 • Gemini AI",
                                    fontSize = 10.sp,
                                    color = CipherGreen
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            bottomBar = {
                if (!activeChatScreenView) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { viewModel.setActiveTab(0) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        val totalUnread = filteredChats.sumOf { it.unreadCount }
                                        if (totalUnread > 0) {
                                            Badge { Text(totalUnread.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chats")
                                }
                            },
                            label = { Text("Chats", fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_chats")
                        )

                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { viewModel.setActiveTab(1) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Suite") },
                            label = { Text("AI Suite", fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_ai_suite")
                        )

                        NavigationBarItem(
                            selected = activeTab == 2,
                            onClick = { viewModel.setActiveTab(2) },
                            icon = { Icon(Icons.Default.Call, contentDescription = "Calls") },
                            label = { Text("Calls", fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_calls")
                        )

                        NavigationBarItem(
                            selected = activeTab == 3,
                            onClick = { viewModel.setActiveTab(3) },
                            icon = { Icon(Icons.Default.Security, contentDescription = "Security") },
                            label = { Text("Security", fontSize = 11.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_security")
                        )

                        NavigationBarItem(
                            selected = activeTab == 4,
                            onClick = { viewModel.setActiveTab(4) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = if (activeTab == 4) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_profile")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    0 -> {
                        if (activeChatScreenView && activeChatId != null) {
                            val activeChatObj = filteredChats.find { it.id == activeChatId }
                            ChatDetailScreen(
                                chat = activeChatObj,
                                messages = activeMessages,
                                isDarkTheme = isDarkTheme,
                                onBackClick = { activeChatScreenView = false },
                                onSendMessage = { text, type, mediaUrl, mediaFileName, replyToId, replyToText ->
                                    viewModel.sendMessage(text, type, mediaUrl, mediaFileName, replyToId, replyToText)
                                },
                                onStartCall = { name, avatar, isVideo ->
                                    viewModel.startCall(name, avatar, isVideo)
                                },
                                onDeleteMessage = { msgId -> viewModel.deleteMessage(msgId) },
                                onToggleStar = { msgId, starred -> viewModel.toggleStar(msgId, starred) },
                                onTogglePin = { msgId, pinned -> viewModel.togglePin(msgId, pinned) }
                            )
                        } else {
                            ChatListScreen(
                                chats = filteredChats,
                                selectedFilter = chatFilterTab,
                                searchQuery = searchQuery,
                                onFilterSelect = { filter -> viewModel.setChatFilterTab(filter) },
                                onSearchChange = { q -> viewModel.setSearchQuery(q) },
                                onChatClick = { chatId ->
                                    viewModel.selectChat(chatId)
                                    activeChatScreenView = true
                                },
                                onNewChatClick = {
                                    viewModel.selectChat("chat_ai")
                                    activeChatScreenView = true
                                }
                            )
                        }
                    }

                    1 -> {
                        AiSuiteScreen(
                            aiFeatures = viewModel.aiFeatures,
                            onRunAiFeature = { feature, input, onResult ->
                                viewModel.runAiFeature(feature, input, onResult)
                            }
                        )
                    }

                    2 -> {
                        CallsScreen(
                            onStartCall = { name, avatar, isVideo ->
                                viewModel.startCall(name, avatar, isVideo)
                            }
                        )
                    }

                    3 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(
                                selectedTabIndex = securityTabSubView,
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Tab(
                                    selected = securityTabSubView == 0,
                                    onClick = { securityTabSubView = 0 },
                                    text = { Text("Security Dashboard", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("security_subtab")
                                )
                                Tab(
                                    selected = securityTabSubView == 1,
                                    onClick = { securityTabSubView = 1 },
                                    text = { Text("Admin Console", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("admin_subtab")
                                )
                            }

                            if (securityTabSubView == 0) {
                                SecurityScreen(
                                    user = currentUser,
                                    securityLogs = securityLogs,
                                    deviceSessions = deviceSessions,
                                    onToggle2FA = { viewModel.toggle2FA() },
                                    onRevokeSession = { sessionId -> viewModel.revokeDeviceSession(sessionId) }
                                )
                            } else {
                                AdminPanelScreen()
                            }
                        }
                    }

                    4 -> {
                        ProfileScreen(
                            user = currentUser,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }

                // Active Call Overlay
                CallingOverlay(
                    callState = callState,
                    onEndCall = { viewModel.endCall() },
                    onToggleMute = { viewModel.toggleMuteCall() },
                    onToggleSpeaker = { viewModel.toggleSpeakerCall() },
                    onToggleScreenShare = { viewModel.toggleScreenShare() },
                    onToggleNoiseSuppression = { viewModel.toggleNoiseSuppression() }
                )
            }
        }
    }
}

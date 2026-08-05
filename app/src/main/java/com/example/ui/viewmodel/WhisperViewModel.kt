package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiCategory
import com.example.data.model.AiFeature
import com.example.data.model.AuthUiState
import com.example.data.model.CallState
import com.example.data.model.Chat
import com.example.data.model.ChatType
import com.example.data.model.DeviceSession
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.data.model.SecurityLog
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.WhisperRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WhisperViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WhisperRepository(application)

    // Auth & User State
    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = "u_me",
            email = "commander@whisper.sec",
            username = "SecurityCommander",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            bio = "🔒 OWASP Top 10 Compliant | E2EE Argon2",
            role = UserRole.ADMIN,
            is2FAEnabled = true,
            activeDevicesCount = 3
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Navigation & Tabs
    private val _activeTab = MutableStateFlow(0) // 0: Chats, 1: AI Suite, 2: Calls, 3: Security & Admin, 4: Profile
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _chatFilterTab = MutableStateFlow(0) // 0: All, 1: Unread, 2: Groups, 3: Channels, 4: Communities, 5: AI
    val chatFilterTab: StateFlow<Int> = _chatFilterTab.asStateFlow()

    // Active Chat
    private val _activeChatId = MutableStateFlow<String?>("chat_alex")
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    // Calls State
    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    // Data Flows from Repository
    val allChats: StateFlow<List<Chat>> = repository.getAllChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredChats: StateFlow<List<Chat>> = combine(allChats, searchQuery, chatFilterTab) { chats, query, filter ->
        chats.filter { chat ->
            val matchesQuery = chat.name.contains(query, ignoreCase = true) || chat.lastMessage.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                1 -> chat.unreadCount > 0
                2 -> chat.type == ChatType.GROUP
                3 -> chat.type == ChatType.CHANNEL
                4 -> chat.type == ChatType.COMMUNITY
                5 -> chat.type == ChatType.AI
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<Message>> = _activeChatId.flatMapLatest { chatId ->
        if (chatId != null) repository.getMessagesForChat(chatId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityLogs: StateFlow<List<SecurityLog>> = repository.getSecurityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceSessions: StateFlow<List<DeviceSession>> = repository.getDeviceSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All AI Features list for the AI Suite Screen
    val aiFeatures = listOf(
        AiFeature("chat_assistant", "AI Chat Assistant", "Conversational AI with system security personas", AiCategory.ASSISTANT, "Chat", "Act as a helpful security assistant."),
        AiFeature("voice_assistant", "AI Voice Assistant", "Voice to text and text to speech interactive AI", AiCategory.ASSISTANT, "Mic", "Listen to user query and speak concise answer."),
        AiFeature("image_gen", "AI Image Generation", "Generate creative visuals & graphics from prompts", AiCategory.CREATIVE, "Image", "Describe visual artwork based on prompt:"),
        AiFeature("image_analysis", "AI Image Analysis", "Inspect images for text, objects & threat vectors", AiCategory.SECURITY, "Scanner", "Analyze image content and summarize key details:"),
        AiFeature("pdf_summarizer", "AI PDF Summarizer", "Extract key takeaways & action items from PDFs", AiCategory.PRODUCTIVITY, "PictureAsPdf", "Summarize the following PDF content into 3 key takeaways:"),
        AiFeature("doc_reader", "AI Document Reader", "Parse large text documents & highlight compliance", AiCategory.PRODUCTIVITY, "Description", "Read document text and highlight key compliance notes:"),
        AiFeature("email_writer", "AI Email Writer", "Compose & refine professional security emails", AiCategory.PRODUCTIVITY, "Email", "Draft a professional email regarding:"),
        AiFeature("code_generator", "AI Code Generator", "Generate production Kotlin, TypeScript, Python code", AiCategory.CODE, "Code", "Generate safe, optimized code for:"),
        AiFeature("code_debugger", "AI Code Debugger", "Locate memory leaks, syntax bugs & security flaws", AiCategory.CODE, "BugReport", "Debug the following code snippet and point out vulnerabilities:"),
        AiFeature("translator", "AI Translator", "Translate messages into 20+ languages seamlessly", AiCategory.PRODUCTIVITY, "Translate", "Translate the following text into French, Spanish, and German:"),
        AiFeature("grammar_checker", "AI Grammar Checker", "Fix syntax, typos & refine message tone", AiCategory.PRODUCTIVITY, "Spellcheck", "Check grammar and improve tone for:"),
        AiFeature("text_rewriter", "AI Text Rewriter", "Rewrite text to formal, concise, or executive tone", AiCategory.CREATIVE, "EditNote", "Rewrite the following message in a formal executive tone:"),
        AiFeature("notes_gen", "AI Notes Generator", "Convert raw thoughts into structured Markdown notes", AiCategory.PRODUCTIVITY, "Notes", "Format raw notes into structured Markdown headers and bullets:"),
        AiFeature("meeting_summary", "AI Meeting Summary", "Transform transcripts into actionable meeting minutes", AiCategory.PRODUCTIVITY, "Groups", "Create meeting minutes with Action Items for:"),
        AiFeature("todo_gen", "AI To-Do Generator", "Generate prioritized checklists from project briefs", AiCategory.PRODUCTIVITY, "Checklist", "Generate a prioritized checklist from:"),
        AiFeature("calendar_assist", "AI Calendar Assistant", "Schedule events & generate iCal invite briefs", AiCategory.PRODUCTIVITY, "CalendarToday", "Generate calendar invite brief for:"),
        AiFeature("reminder_assist", "AI Reminder Assistant", "Smart contextual reminders for critical tasks", AiCategory.PRODUCTIVITY, "Alarm", "Set reminders and notification schedule for:"),
        AiFeature("travel_planner", "AI Travel Planner", "Plan tech conference itineraries & travel guides", AiCategory.CREATIVE, "Flight", "Create a 3-day travel itinerary for:"),
        AiFeature("resume_builder", "AI Resume Builder", "Optimize engineering & security CV bullet points", AiCategory.CAREER, "Work", "Optimize resume summary for role:"),
        AiFeature("cover_letter", "AI Cover Letter", "Generate custom targeted cover letters", AiCategory.CAREER, "Article", "Write a compelling cover letter for:"),
        AiFeature("interview_prep", "AI Interview Prep", "Practice mock technical & system design Q&A", AiCategory.CAREER, "Psychology", "Generate top 3 interview questions and answers for:"),
        AiFeature("ocr_reader", "AI OCR & Text Extract", "Extract text from screenshot images instantly", AiCategory.SECURITY, "CropFree", "Extract all text found in the image data:"),
        AiFeature("web_search", "AI Web Search", "Real-time web search with citations & facts", AiCategory.ASSISTANT, "Search", "Search latest info and summarize with citations for:"),
        AiFeature("chat_memory", "AI Chat Memory", "Long-term memory contextual knowledge graph", AiCategory.ASSISTANT, "Memory", "Recall past context and format memory graph for:")
    )

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Setters & Actions
    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setChatFilterTab(filterIndex: Int) {
        _chatFilterTab.value = filterIndex
    }

    fun selectChat(chatId: String) {
        _activeChatId.value = chatId
        viewModelScope.launch {
            repository.clearUnread(chatId)
        }
    }

    fun sendMessage(
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        mediaFileName: String? = null,
        replyToId: String? = null,
        replyToText: String? = null
    ) {
        val chatId = _activeChatId.value ?: return
        if (text.isBlank() && mediaUrl == null) return

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = text,
                type = type,
                mediaUrl = mediaUrl,
                mediaFileName = mediaFileName,
                replyToId = replyToId,
                replyToText = replyToText
            )
        }
    }

    fun deleteMessage(msgId: String) {
        viewModelScope.launch {
            repository.deleteMessage(msgId)
        }
    }

    fun toggleStar(msgId: String, currentStarred: Boolean) {
        viewModelScope.launch {
            repository.toggleStarMessage(msgId, currentStarred)
        }
    }

    fun togglePin(msgId: String, currentPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinMessage(msgId, currentPinned)
        }
    }

    fun editMessage(msgId: String, newText: String) {
        viewModelScope.launch {
            repository.editMessage(msgId, newText)
        }
    }

    fun startCall(contactName: String, contactAvatar: String, isVideo: Boolean) {
        _callState.value = CallState(
            activeCallId = "call_" + System.currentTimeMillis(),
            contactName = contactName,
            contactAvatar = contactAvatar,
            isVideo = isVideo,
            durationSeconds = 0,
            isConnected = true
        )
    }

    fun endCall() {
        _callState.value = CallState()
    }

    fun toggleMuteCall() {
        _callState.value = _callState.value.copy(isMuted = !_callState.value.isMuted)
    }

    fun toggleSpeakerCall() {
        _callState.value = _callState.value.copy(isSpeakerOn = !_callState.value.isSpeakerOn)
    }

    fun toggleScreenShare() {
        _callState.value = _callState.value.copy(isScreenSharing = !_callState.value.isScreenSharing)
    }

    fun toggleNoiseSuppression() {
        _callState.value = _callState.value.copy(isNoiseSuppressed = !_callState.value.isNoiseSuppressed)
    }

    fun revokeDeviceSession(sessionId: String) {
        viewModelScope.launch {
            repository.revokeDeviceSession(sessionId)
        }
    }

    fun runAiFeature(feature: AiFeature, userInput: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val response = repository.executeAiTool(feature, userInput)
            onComplete(response)
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Auth methods
    fun login(email: String, pass: String) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            if (email.isNotBlank() && pass.length >= 6) {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    isOtpRequired = true
                )
            } else {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    error = "Invalid credentials. Password must be at least 6 characters."
                )
            }
        }
    }

    fun signup(email: String, uname: String, pass: String) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            if (email.contains("@") && uname.isNotBlank() && pass.length >= 6) {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    isOtpRequired = true,
                    email = email,
                    username = uname
                )
            } else {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    error = "Please enter a valid email and username."
                )
            }
        }
    }

    fun verifyOtp(otp: String) {
        if (otp == "123456" || otp.length == 6) {
            _authUiState.value = _authUiState.value.copy(
                isOtpRequired = false,
                is2FARequired = true
            )
        } else {
            _authUiState.value = _authUiState.value.copy(error = "Invalid OTP code. Try 123456")
        }
    }

    fun verify2FA(code: String) {
        if (code == "654321" || code.length == 6) {
            _authUiState.value = _authUiState.value.copy(
                isLoggedIn = true,
                is2FARequired = false
            )
            _currentUser.value = _currentUser.value.copy(
                email = _authUiState.value.email,
                username = _authUiState.value.username
            )
        } else {
            _authUiState.value = _authUiState.value.copy(error = "Invalid 2FA code. Try 654321")
        }
    }

    fun logout() {
        _authUiState.value = AuthUiState(isLoggedIn = false)
    }

    fun toggle2FA() {
        val curr = _currentUser.value
        _currentUser.value = curr.copy(is2FAEnabled = !curr.is2FAEnabled)
    }
}

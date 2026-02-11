package com.badgr.rsvpreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.badgr.rsvpreader.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Screen {
    Splash, Reader, Library, Settings, Outro
}

class MainActivity : ComponentActivity() {
    private val rsvpEngine = RSVPEngine()

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { processBookFile(it) }
    }

    private fun processBookFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader()?.use { it.readText() }
            if (content != null) {
                rsvpEngine.loadText(content)
                Toast.makeText(this, "Book loaded successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Load sample text
        val sampleText = getString(R.string.sample_text)
        rsvpEngine.loadText(sampleText)
        
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.Splash) }
            var isDarkTheme by remember { mutableStateOf(true) }
            
            RSVPReaderTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                        when (screen) {
                            Screen.Splash -> SplashScreen { currentScreen = Screen.Reader }
                            Screen.Reader -> ReaderScreen(
                                rsvpEngine, 
                                lifecycleScope,
                                onNavigateToLibrary = { currentScreen = Screen.Library },
                                onNavigateToSettings = { currentScreen = Screen.Settings },
                                onFinished = { currentScreen = Screen.Outro }
                            )
                            Screen.Library -> LibraryScreen(
                                onBookSelected = { text ->
                                    rsvpEngine.loadText(text)
                                    currentScreen = Screen.Reader
                                },
                                onBack = { currentScreen = Screen.Reader }
                            )
                            Screen.Settings -> SettingsScreen(
                                engine = rsvpEngine,
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme },
                                onBack = { currentScreen = Screen.Reader }
                            )
                            Screen.Outro -> OutroScreen { currentScreen = Screen.Reader }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BADGRBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.badgr_logo),
                contentDescription = "BADGR Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "BADGR Bolt",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = BADGRBlue
            )
            Text(
                text = "Technologies LLC",
                fontSize = 14.sp,
                color = BADGRWhite.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun OutroScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BADGRBlack)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Thank You!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = BADGRBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We hope you enjoyed your reading session with BADGR Speed Reader.",
                fontSize = 18.sp,
                color = BADGRWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = BADGRBlue)
            ) {
                Text("Back to Reader")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    engine: RSVPEngine, 
    scope: kotlinx.coroutines.CoroutineScope,
    onNavigateToLibrary: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onFinished: () -> Unit
) {
    val currentWord by engine.currentWord.collectAsState()
    val orpIndex by engine.orpIndex.collectAsState()
    val progress by engine.progress.collectAsState()
    val wordCount by engine.wordCount.collectAsState()
    val isPlaying by engine.isPlayingState.collectAsState()
    
    var wpm by remember { mutableIntStateOf(engine.getWpm()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.badgr_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BADGR Bolt", color = BADGRBlue, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToLibrary) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = "Library", tint = BADGRBlue)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = BADGRBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = BADGRBlue
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            
            // Main word display area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentWord.isNotEmpty()) {
                    WordDisplay(word = currentWord, orpIndex = orpIndex)
                } else {
                    Text(
                        text = "Press Play to Start",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.3f))
            
            // Progress indicator
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = BADGRBlue,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = wordCount,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // WPM Control
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { 
                        wpm = (wpm - engine.speedIncrement).coerceAtLeast(100)
                        engine.setWpm(wpm)
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BADGRBlue)
                    }
                    Text(
                        text = "$wpm WPM",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BADGRBlue,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { 
                        wpm = (wpm + engine.speedIncrement).coerceAtMost(1500)
                        engine.setWpm(wpm)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = BADGRBlue)
                    }
                }
                Slider(
                    value = wpm.toFloat(),
                    onValueChange = { 
                        wpm = it.toInt()
                        engine.setWpm(wpm)
                    },
                    valueRange = 100f..1500f,
                    colors = SliderDefaults.colors(
                        thumbColor = BADGRBlue,
                        activeTrackColor = BADGRBlue,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { engine.jumpBackward(10) }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Back 10", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }
                
                FloatingActionButton(
                    onClick = {
                        if (isPlaying) engine.pause() else engine.play(scope)
                    },
                    containerColor = BADGRBlue,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                IconButton(onClick = { engine.jumpForward(10) }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Forward 10", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { engine.reset() }) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Progress")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(onBookSelected: (String) -> Unit, onBack: () -> Unit) {
    val books = listOf(
        "Sample Text" to "Welcome to BADGR Speed Reader! This is your rapid serial visual presentation tool. Adjust the speed slider below and press play to begin reading at your optimal pace.",
        "The Great Gatsby" to "In my younger and more vulnerable years my father gave me some advice that I've been turning over in my mind ever since. 'Whenever you feel like criticizing any one,' he told me, 'just remember that all the people in this world haven't had the advantages that you've had.'",
        "A Tale of Two Cities" to "It was the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair.",
        "Moby Dick" to "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world."
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(books) { (title, content) ->
                ListItem(
                    headlineContent = { Text(title) },
                    supportingContent = { Text(content.take(50) + "...", maxLines = 1) },
                    leadingContent = { Icon(Icons.Default.Book, contentDescription = null, tint = BADGRBlue) },
                    modifier = Modifier.clickable { onBookSelected(content) }
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            }
                    item {
                ListItem(
                    headlineContent = { Text("Import from Device", color = BADGRBlue) },
                    leadingContent = { Icon(Icons.Default.FileUpload, contentDescription = null, tint = BADGRBlue) },
                    modifier = Modifier.clickable { 
                        openDocumentLauncher.launch(arrayOf("text/plain"))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    engine: RSVPEngine,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, color = BADGRBlue)
            ListItem(
                headlineContent = { Text("Dark Theme") },
                trailingContent = {
                    Switch(checked = isDarkTheme, onCheckedChange = { onThemeToggle() })
                }
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Reading Engine", style = MaterialTheme.typography.titleMedium, color = BADGRBlue)
            ListItem(
                headlineContent = { Text("Punctuation Delay") },
                supportingContent = { Text("Slow down for commas and periods") },
                trailingContent = {
                    var enabled by remember { mutableStateOf(engine.punctuationDelayEnabled) }
                    Switch(checked = enabled, onCheckedChange = { 
                        enabled = it
                        engine.punctuationDelayEnabled = it
                    })
                }
            )
            ListItem(
                headlineContent = { Text("Speed Increment") },
                supportingContent = { Text("Current: ${engine.speedIncrement} WPM") },
                trailingContent = {
                    Row {
                        IconButton(onClick = { engine.speedIncrement = (engine.speedIncrement - 5).coerceAtLeast(5) }) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }
                        IconButton(onClick = { engine.speedIncrement = (engine.speedIncrement + 5).coerceAtMost(100) }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("About", style = MaterialTheme.typography.titleMedium, color = BADGRBlue)
            ListItem(
                headlineContent = { Text("Version") },
                trailingContent = { Text("1.0.0") }
            )
            ListItem(
                headlineContent = { Text("Developer") },
                trailingContent = { Text("BADGR Technologies LLC") }
            )
        }
    }
}

@Composable
fun WordDisplay(word: String, orpIndex: Int) {
    val annotatedString = buildAnnotatedString {
        val color = MaterialTheme.colorScheme.onSurface
        
        // Text before ORP
        if (orpIndex > 0) {
            withStyle(style = SpanStyle(color = color, fontSize = 48.sp)) {
                append(word.substring(0, orpIndex))
            }
        }
        
        // ORP character (red)
        if (orpIndex < word.length) {
            withStyle(
                style = SpanStyle(
                    color = BADGRRed,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(word[orpIndex])
            }
        }
        
        // Text after ORP
        if (orpIndex < word.length - 1) {
            withStyle(style = SpanStyle(color = color, fontSize = 48.sp)) {
                append(word.substring(orpIndex + 1))
            }
        }
    }
    
    Text(
        text = annotatedString,
        fontSize = 48.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

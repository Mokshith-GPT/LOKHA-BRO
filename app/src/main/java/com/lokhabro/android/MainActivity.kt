package com.lokhabro.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF24272C)
private val Muted = Color(0xFF6B7078)
private val Line = Color(0xFFE0E3E7)
private val Canvas = Color(0xFFFFFFFF)
private val Soft = Color(0xFFF4F5F6)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LokhaTheme { LokhaHome() } }
    }
}

@Composable
private fun LokhaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Ink,
            onPrimary = Color.White,
            background = Canvas,
            onBackground = Ink,
            surface = Canvas,
            onSurface = Ink,
            secondary = Muted,
            outline = Line
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LokhaHome() {
    var input by rememberSaveable { mutableStateOf("") }
    var selectedMode by rememberSaveable { mutableStateOf("Improve") }
    var notice by rememberSaveable { mutableStateOf("Your writing space is ready.") }
    var showSetup by rememberSaveable { mutableStateOf(false) }
    val modes = listOf("Improve", "Professional", "Shorten", "Expand", "Fix grammar")

    Surface(modifier = Modifier.fillMaxSize(), color = Canvas) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.width(42.dp).height(42.dp)
                        .background(Ink, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("L", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp) }
                Spacer(Modifier.width(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("LOKHA BRO", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink, letterSpacing = 1.sp)
                    Text("YOUR WORDS. UPGRADED.", fontSize = 9.sp, color = Muted, letterSpacing = 1.2.sp)
                }
                TextButton(onClick = { showSetup = !showSetup }) { Text("Setup", color = Ink) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Say it better.", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Ink, lineHeight = 39.sp)
                Text("Make every message sound like you — only clearer.", color = Muted, fontSize = 15.sp)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Line, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("YOUR TEXT", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it; notice = "Text updated." },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        placeholder = { Text("Write or paste something here…", color = Muted) },
                        shape = RoundedCornerShape(14.dp)
                    )
                    Text("CHOOSE A STYLE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        modes.chunked(3).forEach { rowModes ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowModes.forEach { mode ->
                                    if (mode == selectedMode) {
                                        Button(
                                            onClick = { selectedMode = mode },
                                            contentPadding = ButtonDefaults.ContentPadding,
                                            colors = ButtonDefaults.buttonColors(containerColor = Ink),
                                            modifier = Modifier.weight(1f)
                                        ) { Text(mode, fontSize = 11.sp) }
                                    } else {
                                        OutlinedButton(
                                            onClick = { selectedMode = mode },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = ButtonDefaults.ContentPadding
                                        ) { Text(mode, fontSize = 11.sp, color = Ink) }
                                    }
                                }
                                repeat(3 - rowModes.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            notice = when {
                                input.isBlank() -> "Add some text first, then choose a style."
                                else -> "AI provider integration is not connected yet. Your text has not been sent anywhere."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Ink)
                    ) {
                        Text("Transform my text", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Text("→", fontSize = 18.sp)
                    }
                }
            }

            if (showSetup) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Soft),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("AI provider setup", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                        Text(
                            "Live AI is not configured in this starter build. Provider selection, secure key storage, and API calls are next implementation steps.",
                            color = Muted, fontSize = 13.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(14.dp)).padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(8.dp).height(8.dp).background(Muted, CircleShape))
                Spacer(Modifier.width(9.dp))
                Text(notice, color = Muted, fontSize = 12.sp)
            }

            Text(
                "Private by default in this prototype · No text is sent to an AI provider yet.",
                color = Muted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

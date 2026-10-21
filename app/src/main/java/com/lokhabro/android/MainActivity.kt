package com.lokhabro.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent {
   val prefs = remember { getSharedPreferences("lokha_settings", MODE_PRIVATE) }
   var key by remember { mutableStateOf(prefs.getString("api_key", "") ?: "") }
   var model by remember { mutableStateOf(prefs.getString("model", "gpt-4o-mini") ?: "gpt-4o-mini") }
   var input by remember { mutableStateOf("") }
   var status by remember { mutableStateOf("Enable LOKHA BRO in Android Settings → Keyboard, then select it as your keyboard.") }
   var busy by remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()
   MaterialTheme {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
     Text("LOKHA BRO", style = MaterialTheme.typography.headlineMedium)
     Text("Your words. Upgraded. Anywhere you type.")
     Text("1. Configure your OpenAI API key. 2. Enable the LOKHA BRO keyboard in system settings. 3. Switch to it in any typing area.")
     OutlinedTextField(value=key,onValueChange={key=it},label={Text("OpenAI API key")},modifier=Modifier.fillMaxWidth(),singleLine=true,visualTransformation=androidx.compose.ui.text.input.PasswordVisualTransformation())
     OutlinedTextField(value=model,onValueChange={model=it},label={Text("Model")},modifier=Modifier.fillMaxWidth(),singleLine=true)
     Button(onClick={
      prefs.edit().putString("api_key",key.trim()).putString("model",model.trim()).apply()
      status=if(key.isBlank()) "Key cleared." else "API key saved on this device. Test a command below."
     }) { Text("Save API settings") }
     Button(onClick={ startActivity(android.content.Intent("android.settings.INPUT_METHOD_SETTINGS")) }) { Text("Enable keyboard in Android settings") }
     Button(onClick={ val imm=getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager; imm.showInputMethodPicker() }) { Text("Choose LOKHA BRO keyboard") }
     Text("Try: hello how are you >fix")
     OutlinedTextField(value=input,onValueChange={input=it},label={Text("Test command")},modifier=Modifier.fillMaxWidth(),minLines=3)
     Button(enabled=!busy,onClick={
      val parsed=Commands.parse(input)
      if(parsed==null){status="End your text with a command such as >fix or >trans-hindi";return@Button}
      busy=true
      scope.launch {
       val result=withContext(Dispatchers.IO){ AiClient.rewrite(prefs,parsed.first,parsed.second) }
       result.onSuccess { input=it;status="Text upgraded." }.onFailure { status=it.message ?: "Request failed." }
       busy=false
      }
     }) { Text(if(busy) "Working…" else "Test transformation") }
     Text(status)
     HorizontalDivider()
     Text("Commands: >fix, >improve, >gen-z, >formal, >casual, >professional, >corporate, >shorten, >expand, >trans-<language>, >undo, >redo.")
     Text("Privacy: keyboard text is sent to OpenAI only when you trigger a rewrite command. Your API key is stored in app-private preferences, not encrypted hardware storage. Do not use this prototype for sensitive text. Requests may take seconds depending on network and model.")
    }
   }
  }
 }
}

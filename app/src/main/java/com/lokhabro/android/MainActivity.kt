package com.lokhabro.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent {
  val prefs=remember{getSharedPreferences("lokha_settings",MODE_PRIVATE)}
  var provider by remember{mutableStateOf(prefs.getString("provider","OpenAI-compatible")?:"OpenAI-compatible")}
  var key by remember{mutableStateOf(prefs.getString("api_key","")?:"")}
  var model by remember{mutableStateOf(prefs.getString("model","gpt-4o-mini")?:"gpt-4o-mini")}
  var endpoint by remember{mutableStateOf(prefs.getString("endpoint","https://api.openai.com/v1")?:"https://api.openai.com/v1")}
  var sample by remember{mutableStateOf("i has completed the work >fix")}
  var status by remember{mutableStateOf("Configure an AI provider, enable accessibility, then type a command in a supported app.")}
  var busy by remember{mutableStateOf(false)}
  val scope=rememberCoroutineScope()
  MaterialTheme { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){
   Text("LOKHA BRO",style=MaterialTheme.typography.headlineMedium)
   Text("Command-powered writing assistant for Android")
   Text("AI PROVIDER",style=MaterialTheme.typography.labelLarge)
   var expanded by remember{mutableStateOf(false)}
   Box { OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth()){Text(provider)};DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}){
    listOf("OpenAI-compatible","Gemini").forEach{DropdownMenuItem(text={Text(it)},onClick={provider=it;expanded=false;if(it=="Gemini"){model="gemini-2.5-flash-lite"}else{model="gpt-4o-mini";endpoint="https://api.openai.com/v1"}})}
   }}
   OutlinedTextField(value=key,onValueChange={key=it},label={Text(if(provider=="Gemini")"Gemini API key" else "Provider API key")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth())
   OutlinedTextField(value=model,onValueChange={model=it},label={Text("Model ID")},singleLine=true,modifier=Modifier.fillMaxWidth())
   if(provider!="Gemini") OutlinedTextField(value=endpoint,onValueChange={endpoint=it},label={Text("API base URL")},singleLine=true,modifier=Modifier.fillMaxWidth())
   Button(onClick={prefs.edit().putString("provider",provider).putString("api_key",key.trim()).putString("model",model.trim()).putString("endpoint",endpoint.trim()).apply();status="Settings saved on this device."},modifier=Modifier.fillMaxWidth()){Text("Save provider settings")}
   OutlinedButton(onClick={startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))},modifier=Modifier.fillMaxWidth()){Text("Enable LOKHA BRO accessibility service")}
   Text("After enabling, allow the service to inspect text fields so it can detect your command suffix. Android and individual apps may restrict access.")
   HorizontalDivider()
   Text("TEST A COMMAND",style=MaterialTheme.typography.labelLarge)
   OutlinedTextField(value=sample,onValueChange={sample=it},label={Text("Text ending in >command")},modifier=Modifier.fillMaxWidth(),minLines=3)
   Button(enabled=!busy,onClick={
    val parsed=Commands.parse(sample)
    if(parsed==null){status="End the text with a supported command, e.g. >fix";return@Button}
    prefs.edit().putString("provider",provider).putString("api_key",key.trim()).putString("model",model.trim()).putString("endpoint",endpoint.trim()).apply()
    busy=true;scope.launch{val result=withContext(Dispatchers.IO){AiClient.rewrite(this@MainActivity,parsed.first,parsed.second)};result.onSuccess{sample=it;status="Transformation succeeded."}.onFailure{status=it.message?:"Request failed."};busy=false}
   },modifier=Modifier.fillMaxWidth()){Text(if(busy)"Transforming…" else "Test transformation")}
   Text(status)
   Text("Commands: >fix, >improve, >gen-z, >formal, >casual, >professional, >corporate, >shorten, >expand, >trans-hindi. Legacy translation syntax ?translate:hi is also parsed.")
   Text("Privacy: when a command is detected, the matching text is sent to the configured provider. API keys are stored in app-private preferences in this prototype, not hardware-backed encrypted storage.")
  }}
 }}
}

package com.lokhabro.android

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object Commands {
 private val suffix = Regex("""(?is)^(.*?)(?:\\s+)(>(?:fix|improve|gen-z|formal|casual|professional|corporate(?:-english)?|short|shorten|expand|trans-[\\p{L}-]+)|\\?translate:[a-z]{2,3})\\s*$""")
 fun parse(text:String):Pair<String,String>? {
  val m=suffix.matchEntire(text) ?: return null
  val body=m.groupValues[1].trimEnd()
  if(body.isBlank()) return null
  var cmd=m.groupValues[2].lowercase()
  if(cmd.startsWith("?translate:")) cmd=">trans-"+cmd.substringAfter(":")
  return body to cmd
 }
 fun instruction(c:String)=when {
  c==">fix" -> "Fix grammar, spelling, and punctuation."
  c==">improve" -> "Improve clarity, flow, and coherence."
  c==">gen-z" -> "Rewrite in a natural Gen Z tone."
  c==">formal" -> "Rewrite in a formal, professional tone."
  c==">casual" -> "Rewrite in a casual, friendly tone."
  c==">professional" -> "Rewrite in a polished professional tone."
  c.startsWith(">corporate") -> "Rewrite in concise corporate English."
  c==">short"||c==">shorten" -> "Make more concise while preserving core meaning."
  c==">expand" -> "Add useful detail without inventing facts."
  c.startsWith(">trans-") -> "Translate into "+c.removePrefix(">trans-")+"."
  else -> error("Unsupported command: $c")
 }
}
object AiClient {
 fun rewrite(context:Context,text:String,command:String):Result<String> = runCatching {
  val p=context.getSharedPreferences("lokha_settings",Context.MODE_PRIVATE)
  val provider=p.getString("provider","OpenAI-compatible")?:"OpenAI-compatible"
  val key=p.getString("api_key","")?.trim().orEmpty()
  val model=p.getString("model","gpt-4o-mini")?.trim().orEmpty()
  val endpoint=p.getString("endpoint","https://api.openai.com/v1")?.trim().orEmpty().trimEnd('/')
  require(key.isNotBlank()){"Set your API key in LOKHA BRO settings."}
  require(model.isNotBlank()){"Enter a model ID in settings."}
  val prompt=Commands.instruction(command)+" Return only the rewritten text. Preserve the original meaning and language unless translation is requested."
  val url=if(provider=="Gemini") "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key" else (if(endpoint.endsWith("/v1")) endpoint else "$endpoint/v1")+"/chat/completions"
  val c=URL(url).openConnection() as HttpURLConnection
  try {
   c.requestMethod="POST";c.connectTimeout=12000;c.readTimeout=45000;c.setRequestProperty("Content-Type","application/json")
   if(provider!="Gemini") c.setRequestProperty("Authorization","Bearer $key")
   c.doOutput=true
   val payload=if(provider=="Gemini") JSONObject().put("systemInstruction",JSONObject().put("parts",JSONArray().put(JSONObject().put("text",prompt)))).put("contents",JSONArray().put(JSONObject().put("role","user").put("parts",JSONArray().put(JSONObject().put("text",text))))) else JSONObject().put("model",model).put("messages",JSONArray().put(JSONObject().put("role","system").put("content",prompt)).put(JSONObject().put("role","user").put("content",text)))
   c.outputStream.use{it.write(payload.toString().toByteArray(Charsets.UTF_8))}
   val status=c.responseCode
   val response=(if(status in 200..299)c.inputStream else c.errorStream)?.bufferedReader()?.use{it.readText()}.orEmpty()
   if(status !in 200..299) error("API error $status: "+runCatching{JSONObject(response).getJSONObject("error").optString("message")}.getOrNull().orEmpty().take(150))
   val answer=if(provider=="Gemini") JSONObject(response).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text") else JSONObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
   answer.trim().also{require(it.isNotEmpty()){"AI returned an empty response."}}
  } finally { c.disconnect() }
 }
}

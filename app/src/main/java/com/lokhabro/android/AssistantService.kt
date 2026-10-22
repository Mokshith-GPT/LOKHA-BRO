package com.lokhabro.android

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Bundle
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AssistantService:AccessibilityService(){
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main)
 private val lock=Mutex()
 private var lastSubmitted:String?=null
 override fun onAccessibilityEvent(event:AccessibilityEvent?){
  if(event==null || event.eventType!=AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)return
  val node=event.source?:return
  val full=node.text?.toString()?.takeIf{it.isNotBlank()}?:return
  val parsed=Commands.parse(full)?:return
  if(lastSubmitted==full)return
  val prefs=getSharedPreferences("lokha_settings",MODE_PRIVATE)
  if(prefs.getString("api_key","").isNullOrBlank())return
  lastSubmitted=full
  val original=parsed.first
  scope.launch {
   lock.withLock {
    val result=withContext(Dispatchers.IO){AiClient.rewrite(this@AssistantService,original,parsed.second)}
    result.onSuccess { rewritten ->
     // Only replace if the field still contains the exact text that triggered this request.
     val current=node.refresh().let{node.text?.toString()}
     if(current==full){
      val args=Bundle().apply{putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,rewritten)}
      node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,args)
     }
    }
    lastSubmitted=null
   }
  }
 }
 override fun onInterrupt(){}
 override fun onServiceConnected(){super.onServiceConnected()}
 override fun onDestroy(){scope.cancel();super.onDestroy()}
}

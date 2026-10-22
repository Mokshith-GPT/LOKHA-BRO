package com.lokhabro.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.*
import android.graphics.Color
import kotlinx.coroutines.*
import java.util.ArrayDeque

class LokhaKeyboardService: InputMethodService() {
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main)
 private var message:TextView?=null
 private val undo=ArrayDeque<String>()
 private val redo=ArrayDeque<String>()
 private var working=false

 override fun onCreateInputView():View {
  val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(5,5,5,5);setBackgroundColor(Color.rgb(242,243,245)) }
  val status=TextView(this).apply { text="LOKHA BRO  •  Type text then >fix + space";textSize=12f;setTextColor(Color.DKGRAY);setPadding(10,5,10,5) }
  message=status;root.addView(status)
  val rows=listOf("qwertyuiop","asdfghjkl","zxcvbnm")
  rows.forEach { letters ->
   val row=LinearLayout(this)
   letters.forEach { char ->
    val b=Button(this).apply { text=char.toString();textSize=15f;setAllCaps(false);minWidth=0;minimumWidth=0;setPadding(0,0,0,0);setOnClickListener { insert(char.toString()) } }
    row.addView(b,LinearLayout.LayoutParams(0,52.dp,1f))
   }
   root.addView(row)
  }
  val actions=LinearLayout(this)
  listOf("123",",","SPACE",".","⌫","↵").forEach { label ->
   val b=Button(this).apply { text=label;setAllCaps(false);textSize=12f;minWidth=0;minimumWidth=0;setPadding(0,0,0,0);setOnClickListener {
    when(label) {
     "123" -> showSymbols(root)
     "SPACE" -> insert(" ")
     "⌫" -> currentInputConnection?.deleteSurroundingText(1,0)
     "↵" -> currentInputConnection?.commitText("\n",1)
     else -> insert(label)
    }
   } }
   actions.addView(b,LinearLayout.LayoutParams(0,54.dp,if(label=="SPACE") 3f else 1f))
  }
  root.addView(actions)
  return root
 }
 private val Int.dp:Int get()=(this*resources.displayMetrics.density).toInt()
 private fun showSymbols(root:LinearLayout) {
  val panel=LinearLayout(this)
  listOf(">", "-", "'", "!", "?", ":", ";", "@", "#").forEach { symbol ->
   val b=Button(this).apply { text=symbol;minWidth=0;minimumWidth=0;setOnClickListener { insert(symbol);root.removeView(panel) } }
   panel.addView(b,LinearLayout.LayoutParams(0,48.dp,1f))
  }
  root.addView(panel,1)
 }
 private fun insert(value:String) {
  val ic=currentInputConnection ?: return
  ic.commitText(value,1)
  if(value==" ") maybeTransform(ic)
 }
 private fun maybeTransform(ic:InputConnection) {
  if(working)return
  // Text before cursor is bounded; IMEs cannot reliably access unlimited document text.
  val before=ic.getTextBeforeCursor(12000,0)?.toString() ?: return
  val parsed=Commands.parse(before) ?: return
  val (original,command)=parsed
  if(command==">undo" || command==">redo") {
   val history=if(command==">undo") undo else redo
   if(history.isEmpty()){message?.text="Nothing to ${command.drop(1)}";return}
   val replacement=history.removeLast()
   (if(command==">undo") redo else undo).addLast(original)
   replace(ic,before,replacement)
   return
  }
  val prefs=getSharedPreferences("lokha_settings",MODE_PRIVATE)
  if(prefs.getString("api_key","").isNullOrBlank()){message?.text="Open LOKHA BRO app to set API key";return}
  working=true;message?.text="Upgrading text…"
  scope.launch {
   val result=withContext(Dispatchers.IO){ AiClient.rewrite(this@LokhaKeyboardService,original,command) }
   result.onSuccess { updated ->
    // Avoid overwriting if user continued typing or changed the field during the request.
    if(currentInputConnection===ic && ic.getTextBeforeCursor(12000,0)?.toString()==before) {
     undo.addLast(original);redo.clear();replace(ic,before,updated);message?.text="Upgraded ✓"
    } else message?.text="Text changed while processing; rewrite not applied"
   }.onFailure { message?.text=it.message?.take(90) ?: "Request failed" }
   working=false
  }
 }
 private fun replace(ic:InputConnection,before:String,updated:String) {
  ic.beginBatchEdit()
  try { ic.deleteSurroundingText(before.length,0);ic.commitText(updated,1) } finally { ic.endBatchEdit() }
 }
 override fun onDestroy(){scope.cancel();super.onDestroy()}
}

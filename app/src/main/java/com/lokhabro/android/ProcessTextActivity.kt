package com.lokhabro.android

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.*

class ProcessTextActivity:Activity(){
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main)
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  val input=intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString().orEmpty()
  val command=intent.getStringExtra("lokha_command")?:">improve"
  if(input.isBlank()){setResult(RESULT_CANCELED);finish();return}
  scope.launch{
   val result=withContext(Dispatchers.IO){AiClient.rewrite(this@ProcessTextActivity,input,command)}
   result.onSuccess{setResult(RESULT_OK,Intent().putExtra(Intent.EXTRA_PROCESS_TEXT,it))}
    .onFailure{Toast.makeText(this@ProcessTextActivity,it.message?: "Rewrite failed",Toast.LENGTH_LONG).show();setResult(RESULT_CANCELED)}
   finish()
  }
 }
 override fun onDestroy(){scope.cancel();super.onDestroy()}
}

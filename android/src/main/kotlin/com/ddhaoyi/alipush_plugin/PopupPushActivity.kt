package com.ddhaoyi.alipush_plugin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.alibaba.sdk.android.push.AndroidPopupActivity
import org.json.JSONObject

class PopupPushActivity :  AndroidPopupActivity(){
    private val handler = Handler(Looper.getMainLooper())
    override fun onSysNoticeOpened(title: String?, summary: String?, extras: MutableMap<String, String>?) {
        Log.d("PopupPushActivity", "onSysNoticeOpened, title: $title, content: $summary, extMap: $extras")
        startActivity(packageManager.getLaunchIntentForPackage(packageName))
        var jsonExtras = JSONObject()
        if (extras != null) {
            for (key in extras.keys){
                jsonExtras.putOpt(key, extras[key])
            }
        }
        Log.d("PopupPushActivity", "onSysNoticeOpened extras: ${jsonExtras.toString()}")
        handler.postDelayed({AlipushHandler.methodChannel?.invokeMethod("onNotificationOpened", mapOf(
                "title" to title,
                "summary" to summary,
                "extras" to jsonExtras.toString()
            ))
            finish()
        }, 1500)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
package com.ddhaoyi.alipush_plugin

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage

class AlipushReceiver : MessageReceiver() {
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 从通知栏打开通知的扩展处理
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    override fun onNotificationOpened(context: Context?, title: String?, summary: String?, extraMap: String?) {
        Log.i("AlipushReceiver", "onNotificationOpened ：  : $title : $summary : $extraMap")
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onNotificationOpened", mapOf(
                    "title" to title,
                    "summary" to summary,
                    "extras" to extraMap
            ))
        },1500)
    }

    /**
     * 通知删除回调
     * @param context
     * @param messageId
     */
    override fun onNotificationRemoved(context: Context?, messageId: String?) {
        Log.i("AlipushReceiver", "onNotificationRemoved ： $messageId")
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onNotificationRemoved", messageId)
        },1500)
    }

    /**
     * 推送通知的回调方法
     * @param context
     * @param title 标题
     * @param summary 内容
     * @param extraMap 参数
     */
    override fun onNotification(context: Context?, title: String?, summary: String?, extraMap: MutableMap<String, String>?) {
        // TODO 处理推送通知
        if(null != extraMap){
            for (entry in extraMap){
                Log.i("AlipushReceiver", "@Get diy param : Key=" + entry.key + " , Value=" + entry.value)
            }
        }else{
            Log.i("AlipushReceiver", "@收到通知 && 自定义消息为空")
        }
        Log.i("AlipushReceiver", "收到一条推送通知, title: $title, summary: $summary, extraMap: $extraMap")
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onNotification", mapOf(
                    "title" to title,
                    "summary" to summary,
                    "extras" to extraMap
            ))
        },1500)
    }

    /**
     * 推送消息的回调方法
     * @param context
     * @param cPushMessage
     */
    override fun onMessage(context: Context?, cPushMessage: CPushMessage?) {
        Log.i("AlipushReceiver", "收到一条推送消息 ： " + cPushMessage!!.title + ", content:" + cPushMessage.content)
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onMessageArrived", mapOf(
                    "appId" to cPushMessage.appId,
                    "content" to cPushMessage.content,
                    "messageId" to cPushMessage.messageId,
                    "title" to cPushMessage.title,
                    "traceInfo" to cPushMessage.traceInfo
            ))
        },1500)
    }

    /**
     * 无动作通知点击回调。当在后台或阿里云控制台指定的通知动作为无逻辑跳转时,通知点击回调为onNotificationClickedWithNoAction而不是onNotificationOpened
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    override fun onNotificationClickedWithNoAction(context: Context?, title: String?, summary: String?, extraMap: String?) {
        Log.i("AlipushReceiver", "onNotificationClickedWithNoAction ：  : $title : $summary : $extraMap")
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onNotificationClickedWithNoAction", mapOf(
                    "title" to title,
                    "summary" to summary,
                    "extras" to extraMap
            ))
        }, 1500)
    }

    /**
     * 应用处于前台时通知到达回调。注意:该方法仅对自定义样式通知有效,相关详情请参考https://help.aliyun.com/document_detail/30066.html?spm=5176.product30047.6.620.wjcC87#h3-3-4-basiccustompushnotification-api
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     * @param openType
     * @param openActivity
     * @param openUrl
     */
    override fun onNotificationReceivedInApp(context: Context?, title: String?, summary: String?, extraMap: MutableMap<String, String>?, openType: Int, openActivity: String?, openUrl: String?) {
        Log.i("AlipushReceiver", "onNotificationReceivedInApp ：  : $title : $summary  $extraMap : $openType : $openActivity : $openUrl")
        handler.postDelayed({
            AlipushHandler.methodChannel?.invokeMethod("onNotificationReceivedInApp", mapOf(
                    "title" to title,
                    "summary" to summary,
                    "extras" to extraMap,
                    "openType" to openType,
                    "openActivity" to openActivity,
                    "openUrl" to openUrl
            ))
        },1500)
    }
}
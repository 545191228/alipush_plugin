package com.ddhaoyi.alipush_plugin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.getSystemService
import anet.channel.util.Utils.context
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** AlipushPlugin */
//class AlipushPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
class AlipushPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var methodChannel: MethodChannel
  //private lateinit var eventChannel: EventChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.ddhaoyi/alipush_plugin")
    AlipushHandler.methodChannel = methodChannel
    methodChannel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method){
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "deviceId" -> result.success(PushServiceFactory.getCloudPushService().deviceId)
      "turnOnPushChannel" -> turnOnPushChannel(result)
      "turnOffPushChannel" -> turnOffPushChannel(result)
      "checkPushChannelStatus" -> checkPushChannelStatus(result)
      "bindAccount" -> bindAccount(call, result)
      "unbindAccount" -> unbindAccount(result)
      "bindTag" -> bindTag(call, result)
      "unbindTag" -> unbindTag(call, result)
      "listTags" -> listTags(call, result)
      "addAlias" -> addAlias(call, result)
      "removeAlias" -> removeAlias(call, result)
      "listAliases" -> listAliases(result)
      "setupNotificationManager" -> setupNotificationManager(call, result)
      "bindPhoneNumber" -> bindPhoneNumber(call, result)
      "unbindPhoneNumber" -> unbindPhoneNumber(result)
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

//  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
//    //to do
//  }
//
//  override fun onCancel(arguments: Any?) {
//    //to do
//  }

  // 开启推送通道
  private fun turnOnPushChannel(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.turnOnPushChannel(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 关闭推送通道
  private fun turnOffPushChannel(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.turnOffPushChannel(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 检查推送通道状态
  private fun checkPushChannelStatus(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.checkPushChannelStatus(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 绑定账号
  private fun bindAccount(call: MethodCall, result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.bindAccount(call.arguments as String?, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 解除账号绑定
  private fun unbindAccount(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.unbindAccount(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 绑定手机号
  private fun bindPhoneNumber(call: MethodCall, result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.bindPhoneNumber(call.arguments as String?, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 解除手机号绑定
  private fun unbindPhoneNumber(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.unbindPhoneNumber(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 绑定标签
  private fun bindTag(call: MethodCall, result: Result) {
    val target = call.argument("target") ?: 1
    val tagsInArrayList = call.argument("tags") ?: arrayListOf<String>()
    val alias = call.argument<String?>("alias")
    val arr = arrayOfNulls<String>(tagsInArrayList.size)
    val tags: Array<String> = tagsInArrayList.toArray(arr)

    val pushService = PushServiceFactory.getCloudPushService()
    pushService.bindTag(target, tags, alias, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 解除标签绑定
  private fun unbindTag(call: MethodCall, result: Result) {
    val target = call.argument("target") ?: 1
    val tagsInArrayList = call.argument("tags") ?: arrayListOf<String>()
    val alias = call.argument<String?>("alias")
    val arr = arrayOfNulls<String>(tagsInArrayList.size)
    val tags: Array<String> = tagsInArrayList.toArray(arr)

    val pushService = PushServiceFactory.getCloudPushService()
    pushService.unbindTag(target, tags, alias, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 标签列表
  private fun listTags(call: MethodCall, result: Result) {
    val target = call.arguments as Int? ?: 1
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.listTags(target, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 添加别名
  private fun addAlias(call: MethodCall, result: Result) {
    val alias = call.arguments as String?
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.addAlias(alias, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 移除别名
  private fun removeAlias(call: MethodCall, result: Result) {
    val alias = call.arguments as String?
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.removeAlias(alias, object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // 别名列表
  private fun listAliases(result: Result) {
    val pushService = PushServiceFactory.getCloudPushService()
    pushService.listAliases(object : CommonCallback {
      override fun onSuccess(response: String?) {
        result.success(mapOf(
                "isSuccessful" to true,
                "response" to response
        ))
      }
      override fun onFailed(errorCode: String?, errorMessage: String?) {
        result.success(mapOf(
                "isSuccessful" to false,
                "errorCode" to errorCode,
                "errorMessage" to errorMessage
        ))
      }
    })
  }

  // Android 8以上设置推送通道
  private fun setupNotificationManager(call: MethodCall, result: Result) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channels = call.arguments as List<Map<String, Any?>>

      // 自定义通知管理
      val mNotificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      // 整理通知通道
      val notificationChannels = mutableListOf<NotificationChannel>()
      for (channel in channels){
        // 通知渠道的id
        val id = channel["id"] ?: context.packageName
        // 用户可以看到的通知渠道的名字.
        val name = channel["name"] ?: context.packageName
        // 用户可以看到的通知渠道的描述
        val description = channel["description"] ?: context.packageName
        val importance = channel["importance"] ?: NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(id as String, name as String, importance as Int)

        // 配置通知渠道的属性
        mChannel.description = description as String
        // 设置通知出现时的闪灯（如果Android设备支持的话）
        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED
        // 设置通知出现时的震动（如果Android设备支持的话）
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationChannels.add(mChannel)
      }

      // 在自定义 NotificationManager 中创建通知渠道
      if (notificationChannels.isNotEmpty()){
        mNotificationManager.createNotificationChannels(notificationChannels)
      }
    }
    result.success(true)
  }


}


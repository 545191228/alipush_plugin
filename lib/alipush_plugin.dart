import 'dart:async';

import 'package:alipush_plugin/src/alipush_callback_result.dart';
import 'package:alipush_plugin/src/alipush_message.dart';
import 'package:alipush_plugin/src/alipush_service_enums.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

// class AlipushPlugin {
//   // 静态化实例
//   static AlipushPlugin _instance;

//   AlipushPlugin.private(this._methodChannel, this._eventChannel);

//   // 工厂模式创建一个实例
//   factory AlipushPlugin() {
//     if (_instance == null) {
//       // flutter与原生通信通道
//       final MethodChannel methodChannel = const MethodChannel('com.ddhaoyi/alipush_plugin');
//       // 原生与flutter通信通道
//       final EventChannel eventChannel = const EventChannel('com.ddhaoyi/alipush_plugin_event');
//       _instance = AlipushPlugin.private(methodChannel, eventChannel);
//     }
//     return _instance;
//   }

//   // flutter与原生通信通道
//   final MethodChannel _methodChannel;
//   // 原生与flutter通信通道
//   final EventChannel _eventChannel;

//   Future<String> get platformVersion async {
//     final String version = await _methodChannel.invokeMethod('getPlatformVersion');
//     return version;
//   }
// }

class AlipushPlugin {
  static const MethodChannel _channel = const MethodChannel('com.ddhaoyi/alipush_plugin');

  AlipushPlugin() {
    _channel.setMethodCallHandler(_handler);
  }

  /// Alipush 初始化返回流监听
  /// 注意可能被多次调用。
  /// Android SDK文档原话如下：
  /// 如果设备成功注册，将回调callback.onSuccess()方法。
  /// 但如果注册服务器连接失败，则调用callback.onFailed方法，并且自动进行重新注册，
  /// 直到onSuccess为止（重试规则会由网络切换等时间自动触发）。
  /// 请在网络通畅的情况下进行相关的初始化调试，如果网络不通，或者App信息配置错误，
  /// 在onFailed方法中，会有相应的错误码返回，可参考错误处理
  ///
  StreamController<AlipushCallbackResult> _initAlipushChannelController =
      StreamController.broadcast();
  Stream<AlipushCallbackResult> get initCloudChannelResult => _initAlipushChannelController.stream;

  /// 服务端推送消息流监听
  /// 用于接收服务端推送的消息，消息不会弹窗，而是回调该方法。
  ///
  StreamController<AlipushMessage> _onMessageRecivedController = StreamController.broadcast();
  Stream<AlipushMessage> get onMessageArrived => _onMessageRecivedController.stream;

  /// 服务端推送通知流监听
  /// 客户端接收到通知后，回调该方法，可获取到并处理通知相关的参数。
  ///
  StreamController<AlipushNotification> _onNotificationRecivedController =
      StreamController.broadcast();
  Stream<AlipushNotification> get onNotification => _onNotificationRecivedController.stream;

  /// 打开通知时会回调该方法，通知打开上报由SDK自动完成。
  ///
  StreamController<OnNotificationOpened> _onNotificationOpenedController =
      StreamController.broadcast();
  Stream<OnNotificationOpened> get onNotificationOpened => _onNotificationOpenedController.stream;

  /// 删除通知时回调该方法，通知删除上报由SDK自动完成。
  ///
  StreamController<String> _onNotificationRemovedController = StreamController.broadcast();
  Stream<String> get onNotificationRemoved => _onNotificationRemovedController.stream;

  /// 打开无跳转逻辑(open=4)通知时回调该方法(v2.3.2及以上版本支持)，通知打开上报由SDK自动完成。
  ///
  StreamController<OnNotificationClickedWithNoAction> _onNotificationClickedWithNoActionController =
      StreamController.broadcast();
  Stream<OnNotificationClickedWithNoAction> get onNotificationClickedWithNoAction =>
      _onNotificationClickedWithNoActionController.stream;

  /// 当用户创建自定义通知样式，并且设置推送应用内到达不创建通知弹窗时调用该回调，
  /// 且此时不调用onNotification回调(v2.3.3及以上版本支持)
  ///
  StreamController<OnNotificationReceivedInApp> _onNotificationReceivedInAppController =
      StreamController.broadcast();
  Stream<OnNotificationReceivedInApp> get onNotificationReceivedInApp =>
      _onNotificationReceivedInAppController.stream;

  /// 系统版本获取
  ///
  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///确保注册成功以后调用获取[deviceId]
  ///
  Future<String> get deviceId async {
    return _channel.invokeMethod("deviceId");
  }

  /// 获取推送通道状态
  ///
  Future<AlipushCallbackResult> get pushChannelStatus async {
    //var result = await _channel.invokeMethod("checkPushChannelStatus");
    final Map<String, dynamic> result = await _channel.invokeMethod("checkPushChannelStatus");
    return _handleResult(result);
    // var result = await _channel.invokeMethod("checkPushChannelStatus");
    // return AlipushCallbackResult(
    //   isSuccessful: result["isSuccessful"],
    //   response: result["response"],
    //   errorCode: result["errorCode"],
    //   errorMessage: result["errorMessage"],
    // );
  }

  /// 开启推送通道
  ///
  Future<AlipushCallbackResult> turnOnPushChannel() async {
    // var result = await _channel.invokeMethod("turnOnPushChannel");
    final Map<String, dynamic> result = await _channel.invokeMethod("turnOnPushChannel");
    return _handleResult(result);
  }

  /// 关闭推送通道
  ///
  Future<AlipushCallbackResult> turnOffPushChannel() async {
    // var result = await _channel.invokeMethod("turnOffPushChannel");
    final Map<String, dynamic> result = await _channel.invokeMethod("turnOffPushChannel");
    return _handleResult(result);
  }

  /// 账号绑定
  /// 将应用内账号和推送通道相关联，可以实现按账号的定点消息推送；
  /// 设备只能绑定一个账号，同一账号可以绑定到多个设备；
  /// 同一设备更换绑定账号时无需进行解绑，重新调用绑定账号接口即可生效；
  /// 若业务场景需要先解绑后绑定，在解绑账号成功回调中进行绑定绑定操作，以此保证执行的顺序性；
  /// 账户名设置支持64字节。
  ///
  Future<AlipushCallbackResult> bindAccount(String account) async {
    assert(account != null);
    //var result = await _channel.invokeMethod("bindAccount", account);
    final Map<String, dynamic> result = await _channel.invokeMethod("bindAccount", account);
    return _handleResult(result);
  }

  /// 解除账号绑定
  ///
  Future<AlipushCallbackResult> unbindAccount() async {
    // var result = await _channel.invokeMethod("unbindAccount");
    final Map<String, dynamic> result = await _channel.invokeMethod("unbindAccount");
    return _handleResult(result);
  }

  /// 绑定标签到指定目标；
  /// 支持向设备、账号和别名绑定标签，绑定类型由参数target指定；
  /// 绑定标签在10分钟内生效；
  /// App最多支持绑定1万个标签，单个标签最大支持128字符。
  /// target 目标类型，1：本设备； 2：本设备绑定账号； 3：别名
  /// target(V2.3.5及以上版本) 目标类型，
  /// CloudPushService.DEVICE_TARGET：本设备；
  /// CloudPushService.ACCOUNT_TARGET：本账号；
  /// CloudPushService.ALIAS_TARGET：别名
  /// tags 标签（数组输入）
  /// alias 别名（仅当target = 3时生效）
  /// callback 回调
  ///
  Future<AlipushCallbackResult> bindTag({
    @required AlipushServiceTarget target,
    List<String> tags,
    String alias,
  }) async {
    // var result = await _channel.invokeMethod(
    //     "bindTag", {"target": target.index + 1, "tags": tags ?? List<String>(), "alias": alias});
    final Map<String, dynamic> result = await _channel.invokeMethod(
        "bindTag", {"target": target.index + 1, "tags": tags ?? List<String>(), "alias": alias});
    return _handleResult(result);
  }

  /// 解绑指定目标标签；
  /// 支持解绑设备、账号和别名标签，解绑类型由参数target指定；
  /// 解绑标签在10分钟内生效；
  /// 解绑标签只是解除设备和标签的绑定关系，不等同于删除标签，即该APP下标签仍然存在，系统目前不支持标签的删除。
  /// target 目标类型，1：本设备； 2：本设备绑定账号； 3：别名
  /// target(V2.3.5及以上版本) 目标类型，
  /// CloudPushService.DEVICE_TARGET：本设备；
  /// CloudPushService.ACCOUNT_TARGET：本账号；
  /// CloudPushService.ALIAS_TARGET：别名
  /// tags 标签（数组输入）
  /// alias 别名（仅当target = 3时生效）
  /// callback 回调
  ///
  Future<AlipushCallbackResult> unbindTag(
      {@required AlipushServiceTarget target, List<String> tags, String alias}) async {
    // var result = await _channel.invokeMethod(
    //     "unbindTag", {"target": target.index + 1, "tags": tags ?? List<String>(), "alias": alias});
    final Map<String, dynamic> result = await _channel.invokeMethod(
        "unbindTag", {"target": target.index + 1, "tags": tags ?? List<String>(), "alias": alias});

    return _handleResult(result);
  }

  /// 查询目标绑定标签，当前仅支持查询设备标签；
  /// 查询结果可从回调onSuccess(response)的response获取；
  /// 标签绑定成功且生效（10分钟内）后即可查询。
  ///
  Future<AlipushCallbackResult> listTags(AlipushServiceTarget target) async {
    //var result = await _channel.invokeMethod("listTags", target.index + 1);
    final Map<String, dynamic> result = await _channel.invokeMethod("listTags", target.index + 1);
    return _handleResult(result);
  }

  /// 添加别名
  /// 设备添加别名；
  /// 单个设备最多添加128个别名，且同一别名最多添加到128个设备；
  /// 别名支持128字节。
  ///
  Future<AlipushCallbackResult> addAlias(String alias) async {
    //var result = await _channel.invokeMethod("addAlias", alias);
    final Map<String, dynamic> result = await _channel.invokeMethod("addAlias", alias);
    return _handleResult(result);
  }

  /// 删除别名
  /// 删除设备别名；
  /// 支持删除指定别名和删除全部别名（alias = null || alias.length = 0）
  ///
  Future<AlipushCallbackResult> removeAlias(String alias) async {
    //var result = await _channel.invokeMethod("removeAlias", alias);
    final Map<String, dynamic> result = await _channel.invokeMethod("removeAlias", alias);
    return _handleResult(result);
  }

  /// 查询设备别名；
  /// 查询结果可从回调onSuccess(response)的response中获取；
  /// 从V3.0.9及以上版本开始，接口内部有5s短缓存，5s内多次调用只会请求服务端一次。
  ///
  Future<AlipushCallbackResult> listAliases() async {
    //var result = await _channel.invokeMethod("listAliases");
    final Map<String, dynamic> result = await _channel.invokeMethod("listAliases");
    return _handleResult(result);
  }

  /// 自定义推送通道
  /// 这个方法只对android有效
  /// 最好调用这个方法以保证在Android 8以上推送通知好用。
  /// 如果不调用这个方法，请确认你自己创建了NotificationChannel。
  /// 为了更好的用户体验，一些参数请不要用传[null]。
  /// [id]一定要和后台推送时候设置的通知通道一样，否则Android8.0以上无法完成通知推送。
  ///
  Future setupNotificationManager(List<NotificationChannel> channels) async {
    return _channel.invokeMethod(
        "setupNotificationManager", channels.map((e) => e.toJson()).toList());
  }

  /// 自定义推送通道
  /// 这个方法仅针对iOS
  /// 设置推送通知显示方式
  /// completionHandler(UNNotificationPresentationOptionSound | UNNotificationPresentationOptionAlert | UNNotificationPresentationOptionBadge);
  ///
  Future configureNotificationPresentationOption(
      {bool none: false, bool sound: true, bool alert: true, bool badge: true}) async {
    return _channel.invokeMethod("configureNotificationPresentationOption",
        {"none": none, "sound": sound, "alert": alert, "badge": badge});
  }

  /// 返回值处理
  ///
  AlipushCallbackResult _handleResult(dynamic result) {
    return AlipushCallbackResult(
      isSuccessful: result["isSuccessful"],
      response: result["response"],
      errorCode: result["errorCode"],
      errorMessage: result["errorMessage"],
      iosError: result["iosError"],
    );
  }

  Future<dynamic> _handler(MethodCall methodCall) {
    if ("initCloudChannelResult" == methodCall.method) {
      _initAlipushChannelController.add(AlipushCallbackResult(
        isSuccessful: methodCall.arguments["isSuccessful"],
        response: methodCall.arguments["response"],
        errorCode: methodCall.arguments["errorCode"],
        errorMessage: methodCall.arguments["errorMessage"],
      ));
    } else if ("onMessageArrived" == methodCall.method) {
      _onMessageRecivedController.add(AlipushMessage(
        messageId: methodCall.arguments["messageId"],
        appId: methodCall.arguments["appId"],
        title: methodCall.arguments["title"],
        content: methodCall.arguments["content"],
        traceInfo: methodCall.arguments["traceInfo"],
      ));
    } else if ("onNotification" == methodCall.method) {
      _onNotificationRecivedController.add(AlipushNotification(methodCall.arguments["title"],
          methodCall.arguments["summary"], methodCall.arguments["extras"]));
    } else if ("onNotificationOpened" == methodCall.method) {
      _onNotificationOpenedController.add(OnNotificationOpened(
          methodCall.arguments["title"],
          methodCall.arguments["summary"],
          methodCall.arguments["extras"],
          methodCall.arguments["subtitle"],
          methodCall.arguments["badge"]));
    } else if ("onNotificationRemoved" == methodCall.method) {
      _onNotificationRemovedController.add(methodCall.arguments);
    } else if ("onNotificationClickedWithNoAction" == methodCall.method) {
      _onNotificationClickedWithNoActionController.add(OnNotificationClickedWithNoAction(
          methodCall.arguments["title"],
          methodCall.arguments["summary"],
          methodCall.arguments["extras"]));
    } else if ("onNotificationReceivedInApp" == methodCall.method) {
      _onNotificationReceivedInAppController.add(OnNotificationReceivedInApp(
          methodCall.arguments["title"],
          methodCall.arguments["summary"],
          methodCall.arguments["extras"],
          methodCall.arguments["openType"],
          methodCall.arguments["openActivity"],
          methodCall.arguments["openUrl"]));
    }

    return Future.value(true);
  }

  /// 析构函数
  ///
  dispose() {
    _initAlipushChannelController.close();
    _onMessageRecivedController.close();
    _onNotificationRecivedController.close();
    _onNotificationOpenedController.close();
    _onNotificationRemovedController.close();
    _onNotificationClickedWithNoActionController.close();
    _onNotificationReceivedInAppController.close();
  }
}

class NotificationChannel {
  const NotificationChannel(this.id, this.name, this.description,
      {this.importance = AndroidNotificationImportance.HIGH});
  final String id;
  final String name;
  final String description;
  final AndroidNotificationImportance importance;

  Map<String, dynamic> toJson() {
    return {"id": id, "name": name, "description": description, "importance": importance.index + 1};
  }
}

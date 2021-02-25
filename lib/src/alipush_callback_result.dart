class AlipushCallbackResult {
  final bool isSuccessful;
  final String response;
  final String errorCode;
  final String errorMessage;
  final String iosError;

  AlipushCallbackResult({
    this.isSuccessful,
    this.response,
    this.errorCode,
    this.errorMessage,
    this.iosError,
  });
}

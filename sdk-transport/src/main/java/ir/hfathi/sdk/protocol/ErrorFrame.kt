package ir.hfathi.sdk.protocol

data class ErrorFrame(
    val errorCode: ErrorCode,
    val message: String,
    val details: Map<String, String> = emptyMap()
)
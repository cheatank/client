package injects

/**
 * エラーメッセージ
 */
data class ErrorMessage(val message: String?) {
    constructor(exception: Exception) : this(exception.message)
}

package injects

import com.soywiz.korge.view.Text

/**
 * メッセージ
 */
data class Message(val content: String?, val isError: Boolean = false) {
    constructor(exception: Exception) : this(exception.message, true)

    companion object {
        @Suppress("FunctionName")
        fun Message?.Text(textSize: Double): Text {
            return when {
                this == null -> Text("", textSize)
                isError -> Text(content.orEmpty(), textSize, Theme.ErrorText)
                else -> Text(content.orEmpty(), textSize, Theme.Text)
            }
        }
    }
}

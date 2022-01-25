package injects

enum class CheatMode {
    Enable,
    Disable;

    val isEnable
        get() = this == Enable

    companion object {
        fun from(enable: Boolean) = if (enable) Enable else Disable
    }
}

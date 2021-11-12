import com.soywiz.korge.Korge
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.Colors

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
    val rect = roundRect(100, 100, 100, 100)
    rect.addUpdater {
        handleInputKey(input)
    }
}

package scene

import Theme
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.BoxUISkin
import com.soywiz.korge.ui.uiTextInput
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.alignTopToBottomOf
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.container
import com.soywiz.korge.view.image
import com.soywiz.korge.view.roundRect
import com.soywiz.korge.view.size
import com.soywiz.korge.view.text
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import injects.ErrorMessage

/**
 * タイトル画面
 *
 * @property errorMessage エラーメッセージ
 */
class TitleScene(private val errorMessage: ErrorMessage?) : Scene() {
    @OptIn(KorgeExperimental::class)
    override suspend fun Container.sceneInit() {
        var address = "ws://localhost:8080"
        text(errorMessage?.message.orEmpty(), 12.0, Theme.ErrorText).centerXOnStage()
        container {
            val title = text("CheaTank", 48.0, Theme.Text) {
                centerXOnStage()
            }
            val logo = image(resourcesVfs["icon.png"].readBitmap()) {
                size(128.0, 128.0)
                centerXOnStage()
                alignTopToBottomOf(title, 32.0)
            }
            val startButton = roundRect(192.0, 64.0, 5.0, fill = Theme.ButtonBackGround) {
                centerXOnStage()
                alignTopToBottomOf(logo, 32.0)
                onClick {
                    launchImmediately { sceneContainer.changeTo<WaitingScene>(address) }
                }
                text("Play", 32.0, Theme.Text).centerOn(this)
            }
            uiTextInput(
                address,
                width = 256.0,
                skin = BoxUISkin(
                    bgColor = Theme.BackGround,
                    borderColor = Theme.Text,
                )
            ) {
                centerXOnStage()
                alignTopToBottomOf(startButton, 16.0)
                onTextUpdated {
                    address = it.text
                }
            }
        }.centerOnStage()
    }
}

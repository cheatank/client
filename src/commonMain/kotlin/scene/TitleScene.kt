package scene

import Theme
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
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

/**
 * タイトル画面
 */
class TitleScene : Scene() {
    override suspend fun Container.sceneInit() {
        container {
            val title = text("CheaTank", 48.0, Theme.Text) {
                centerXOnStage()
            }
            val logo = image(resourcesVfs["icon.png"].readBitmap()) {
                size(128.0, 128.0)
                centerXOnStage()
                alignTopToBottomOf(title, 48.0)
            }
            roundRect(192.0, 64.0, 5.0, fill = Theme.ButtonBackGround) {
                centerXOnStage()
                alignTopToBottomOf(logo, 48.0)
                onClick {
                    launchImmediately { sceneContainer.changeTo<WaitingScene>() }
                }
                text("Play", 32.0, Theme.Text).centerOn(this)
            }
        }.centerOnStage()
    }
}

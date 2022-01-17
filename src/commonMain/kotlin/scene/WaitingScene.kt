package scene

import Theme
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.async.launchImmediately
import kotlinx.coroutines.delay

/**
 * 待機画面
 */
class WaitingScene : Scene() {
    override suspend fun Container.sceneInit() {
        text("Waiting...", 48.0, Theme.Text).centerOnStage()
        launchImmediately { // TODO 接続処理
            delay(3000)
            sceneContainer.changeTo<GameScene>()
        }
    }
}

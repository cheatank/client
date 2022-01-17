import com.soywiz.korge.scene.Module
import com.soywiz.korinject.AsyncInjector
import scene.GameScene
import scene.TitleScene
import scene.WaitingScene

/**
 * 画面一覧
 */
object SceneModule : Module() {
    override val mainScene = TitleScene::class
    override val title = "CheaTank"
    override val bgcolor = Theme.BackGround

    override suspend fun AsyncInjector.configure() {
        mapPrototype { TitleScene() }
        mapPrototype { WaitingScene() }
        mapPrototype { GameScene() }
    }
}

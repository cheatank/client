import com.soywiz.korge.scene.Module
import com.soywiz.korinject.AsyncInjector
import scene.GameScene
import scene.TitleScene

/**
 * 画面一覧
 */
object SceneModule : Module() {
    override val mainScene = TitleScene::class
    override val title = "CheaTank"
    override var bgcolor = Theme.BackGround

    override suspend fun AsyncInjector.configure() {
        mapPrototype { TitleScene(getOrNull()) }
        mapPrototype { GameScene(get(), get()) }
    }
}

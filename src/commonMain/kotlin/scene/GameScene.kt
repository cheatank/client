package scene

import Theme
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.data.ConfigData
import com.github.cheatank.common.data.ShortData
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korio.async.launchImmediately
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import util.readRawPacket

/**
 * ゲーム画面
 */
class GameScene(private val session: DefaultClientWebSocketSession, private val configData: ConfigData) : Scene() {
    override suspend fun Container.sceneInit() {
        val time = ObservableProperty(configData.timeLimit)
        text("${time.value}", 36.0, Theme.Text) {
            centerXOnStage()
            time.observe {
                text = "${time.value}"
            }
        }
        launchImmediately {
            while (true) {
                for (frame in session.incoming) {
                    when (frame) {
                        is Frame.Binary -> {
                            println("pass")
                            val packet = frame.readRawPacket() ?: continue
                            when (packet.id) {
                                PacketType.Countdown.id -> {
                                    val data = packet.toPacket(PacketType.Countdown)?.data as? ShortData ?: continue
                                    time.value = data.short
                                }
                            }
                        }
                        is Frame.Close -> {
                            session.close()
                        }
                    }
                }
            }
        }
    }
}

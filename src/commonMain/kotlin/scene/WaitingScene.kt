package scene

import Theme
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.PacketVersion
import com.github.cheatank.common.data.EmptyPacketData
import com.github.cheatank.common.data.IntData
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.async.launchImmediately
import httpClient
import injects.ErrorMessage
import io.ktor.client.features.websocket.webSocket
import util.receivePacket
import util.sendPacket

/**
 * 待機画面
 *
 * @property address サーバーアドレス
 */
class WaitingScene(private val address: String) : Scene() {
    override suspend fun Container.sceneInit() {
        text("Waiting...", 48.0, Theme.Text).centerOnStage()
        launchImmediately {
            try {
                httpClient.webSocket(address) {
                    outgoing.sendPacket(PacketType.GetVersion, EmptyPacketData)
                    val packet = incoming.receivePacket(PacketType.SendVersion)
                    val packetData = packet?.data
                    if (packetData is IntData) {
                        if (packetData.int != PacketVersion) {
                            throw IllegalStateException("Packet version do not match.")
                        } else {
                            outgoing.sendPacket(PacketType.JoinQueue, EmptyPacketData)
                            incoming.receivePacket(PacketType.StartGame)
                        }
                    } else {
                        throw IllegalStateException("Failed to receive the version packet.")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                sceneContainer.changeTo<TitleScene>(ErrorMessage(ex))
            }
            sceneContainer.changeTo<GameScene>()
        }
    }
}
